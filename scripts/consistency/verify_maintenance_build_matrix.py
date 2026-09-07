#!/usr/bin/env python3
"""Validate every ddd4j-boot maintenance line against its build contract."""

from __future__ import annotations

import argparse
import csv
import re
import subprocess
import sys
import xml.etree.ElementTree as element_tree
from pathlib import Path


MATRIX_COLUMNS = [
    "branch",
    "jdk",
    "boot_version",
    "ddd4j_version",
    "revision",
    "maven_major",
    "model_version",
    "pom_namespace",
    "aggregator_container",
    "aggregator_item",
]
JAVA_VERSION_PATTERN = re.compile(r"java-version:\s*['\"]?([0-9]+)['\"]?")
MAVEN_VERSION_PATTERN = re.compile(r"apache-maven-([0-9]+(?:\.[0-9A-Za-z-]+)+)-bin\.zip")


def local_name(tag: str) -> str:
    return tag.rsplit("}", 1)[-1]


def namespace(tag: str) -> str:
    return tag[1:].split("}", 1)[0] if tag.startswith("{") else ""


def child_text(root: element_tree.Element, name: str) -> str | None:
    for item in root:
        if local_name(item.tag) == name and item.text:
            return item.text.strip()
    return None


def properties(root: element_tree.Element) -> dict[str, str]:
    for item in root:
        if local_name(item.tag) == "properties":
            return {local_name(value.tag): (value.text or "").strip() for value in item}
    return {}


def git(repo: Path, *arguments: str) -> str:
    result = subprocess.run(
        ["git", "-C", str(repo), *arguments],
        text=True,
        capture_output=True,
        check=False,
    )
    if result.returncode != 0:
        raise ValueError(f"git {' '.join(arguments)} failed: {result.stderr.strip()}")
    return result.stdout


def read_matrix(path: Path) -> list[dict[str, str]]:
    with path.open(encoding="utf-8", newline="") as source:
        reader = csv.DictReader(source, delimiter="\t")
        if reader.fieldnames != MATRIX_COLUMNS:
            raise ValueError(f"invalid build matrix header: {path}")
        rows = [{key: value.strip() for key, value in row.items()} for row in reader]
    if len(rows) != 13:
        raise ValueError(f"expected 13 maintenance lines, got {len(rows)}")
    return rows


def parse_pom(content: str, source: str) -> element_tree.Element:
    try:
        return element_tree.fromstring(content)
    except element_tree.ParseError as error:
        raise ValueError(f"invalid XML in {source}: {error}") from error


def validate_root_pom(row: dict[str, str], root: element_tree.Element, errors: list[str]) -> None:
    branch = row["branch"]
    actual_properties = properties(root)
    expected_properties = {
        "revision": row["revision"],
        "ddd4j.version": row["ddd4j_version"],
        "spring-boot.version": row["boot_version"],
        "java.version": row["jdk"] if row["jdk"] != "8" else "1.8",
    }
    if namespace(root.tag) != row["pom_namespace"]:
        errors.append(f"{branch}: root POM namespace is {namespace(root.tag)!r}, expected {row['pom_namespace']!r}")
    if child_text(root, "modelVersion") != row["model_version"]:
        errors.append(f"{branch}: root modelVersion must be {row['model_version']}")
    for name, expected in expected_properties.items():
        if actual_properties.get(name) != expected:
            errors.append(f"{branch}: {name} is {actual_properties.get(name)!r}, expected {expected!r}")
    container = next((item for item in root if local_name(item.tag) == row["aggregator_container"]), None)
    if container is None:
        errors.append(f"{branch}: missing root {row['aggregator_container']} element")
        return
    children = [local_name(item.tag) for item in container]
    if not children:
        errors.append(f"{branch}: root {row['aggregator_container']} is empty")
    unexpected = sorted({name for name in children if name != row["aggregator_item"]})
    if unexpected:
        errors.append(
            f"{branch}: {row['aggregator_container']} contains {unexpected}, expected only {row['aggregator_item']}"
        )


def validate_all_poms(
    repo: Path,
    ref: str,
    row: dict[str, str],
    paths: list[str],
    errors: list[str],
) -> None:
    branch = row["branch"]
    pom_paths = (
        value
        for value in paths
        if value.endswith("pom.xml") and not value.startswith("scripts/consistency/fixtures/")
    )
    for path in pom_paths:
        root = parse_pom(git(repo, "show", f"{ref}:{path}"), f"{ref}:{path}")
        if namespace(root.tag) != row["pom_namespace"]:
            errors.append(f"{branch}: {path} uses namespace {namespace(root.tag)!r}")
        if child_text(root, "modelVersion") != row["model_version"]:
            errors.append(f"{branch}: {path} does not use modelVersion {row['model_version']}")
        for container_name in ("modules", "subprojects"):
            container = next((item for item in root if local_name(item.tag) == container_name), None)
            if container is None:
                continue
            if container_name != row["aggregator_container"]:
                errors.append(f"{branch}: {path} uses unexpected aggregator {container_name}")
                continue
            invalid = sorted({local_name(item.tag) for item in container if local_name(item.tag) != row["aggregator_item"]})
            if invalid:
                errors.append(f"{branch}: {path} has invalid {container_name} children {invalid}")


def validate_wrapper(repo: Path, ref: str, row: dict[str, str], errors: list[str]) -> None:
    content = git(repo, "show", f"{ref}:.mvn/wrapper/maven-wrapper.properties")
    match = MAVEN_VERSION_PATTERN.search(content)
    if not match:
        errors.append(f"{row['branch']}: Maven wrapper distribution version is missing")
        return
    actual_major = match.group(1).split(".", 1)[0]
    if actual_major != row["maven_major"]:
        errors.append(
            f"{row['branch']}: Maven wrapper {match.group(1)} has major {actual_major}, expected {row['maven_major']}"
        )


def validate_workflows(repo: Path, ref: str, row: dict[str, str], errors: list[str]) -> None:
    for path in (".github/workflows/verify.yml", ".github/workflows/deploy.yml"):
        content = git(repo, "show", f"{ref}:{path}")
        versions = sorted(set(JAVA_VERSION_PATTERN.findall(content)))
        if versions != [row["jdk"]]:
            errors.append(f"{row['branch']}: {path} Java versions are {versions}, expected {[row['jdk']]}")
        if "MAVEN_SETTINGS_XML" not in content:
            errors.append(f"{row['branch']}: {path} does not consume MAVEN_SETTINGS_XML")


def validate_branch(repo: Path, prefix: str, row: dict[str, str], errors: list[str]) -> None:
    ref = f"{prefix}{row['branch']}"
    root = parse_pom(git(repo, "show", f"{ref}:pom.xml"), f"{ref}:pom.xml")
    paths = git(repo, "ls-tree", "-r", "--name-only", ref).splitlines()
    validate_root_pom(row, root, errors)
    validate_all_poms(repo, ref, row, paths, errors)
    validate_wrapper(repo, ref, row, errors)
    validate_workflows(repo, ref, row, errors)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--repo", type=Path, required=True)
    parser.add_argument("--matrix", type=Path, required=True)
    parser.add_argument("--ref-prefix", default="origin/")
    args = parser.parse_args()
    try:
        rows = read_matrix(args.matrix)
        errors: list[str] = []
        for row in rows:
            validate_branch(args.repo, args.ref_prefix, row, errors)
    except ValueError as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1
    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        print(f"FAIL: {len(errors)} maintenance build matrix violations", file=sys.stderr)
        return 1
    print(f"PASS: {len(rows)} maintenance lines match the build matrix")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
