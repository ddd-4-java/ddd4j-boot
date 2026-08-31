#!/usr/bin/env python3
"""Create reproducible POM/module/registration baselines from Git refs."""

from __future__ import annotations

import argparse
import csv
import json
import re
import subprocess
import sys
import xml.etree.ElementTree as element_tree
from pathlib import Path


BRANCH_COLUMNS = ["branch", "jdk_group", "boot_line", "ddd4j_line", "registration_mode"]
PROPERTY_PATTERN = re.compile(r"^\$\{(?P<name>[^}]+)}$")


def fail(message: str) -> None:
    raise ValueError(message)


def local_name(element: element_tree.Element) -> str:
    return element.tag.rsplit("}", 1)[-1]


def child_text(element: element_tree.Element, name: str) -> str | None:
    for child in element:
        if local_name(child) == name and child.text:
            return child.text.strip()
    return None


def child(element: element_tree.Element, name: str) -> element_tree.Element | None:
    return next((item for item in element if local_name(item) == name), None)


def resolve(value: str | None, properties: dict[str, str]) -> str | None:
    if value is None:
        return None
    match = PROPERTY_PATTERN.match(value)
    return properties.get(match.group("name"), value) if match else value


def parse_pom(content: str, source_name: str) -> dict[str, object]:
    try:
        root = element_tree.fromstring(content)
    except element_tree.ParseError as error:
        fail(f"invalid XML in {source_name}: {error}")
    properties_element = child(root, "properties")
    property_elements = list(properties_element) if properties_element is not None else []
    properties = {
        local_name(item): (item.text or "").strip()
        for item in property_elements
    }
    java_version = next(
        (resolve(properties.get(key), properties) for key in ("java.version", "maven.compiler.release", "maven.compiler.source") if properties.get(key)),
        None,
    )
    if not java_version:
        fail(f"missing Java version in {source_name}")
    parent = child(root, "parent")
    parent_group = child_text(parent, "groupId") if parent is not None else None
    parent_artifact = child_text(parent, "artifactId") if parent is not None else None
    parent_version = resolve(child_text(parent, "version"), properties) if parent is not None else None
    modules_element = child(root, "modules")
    subprojects_element = child(root, "subprojects")
    module_elements = list(modules_element) if modules_element is not None else []
    subproject_elements = list(subprojects_element) if subprojects_element is not None else []
    if module_elements:
        root_module_kind = "modules"
        modules = [item.text.strip() for item in module_elements if local_name(item) == "module" and item.text]
    elif subproject_elements:
        root_module_kind = "subprojects"
        modules = [item.text.strip() for item in subproject_elements if local_name(item) == "subproject" and item.text]
    else:
        root_module_kind = "none"
        modules = []
    boot_bom_version = None
    for dependency in root.iter():
        if local_name(dependency) != "dependency":
            continue
        if child_text(dependency, "groupId") == "org.springframework.boot" and child_text(dependency, "artifactId") == "spring-boot-dependencies":
            boot_bom_version = resolve(child_text(dependency, "version"), properties)
            break
    spring_boot_version = parent_version if parent_group == "org.springframework.boot" else boot_bom_version or resolve(properties.get("spring-boot.version"), properties)
    return {
        "revision": resolve(properties.get("revision"), properties),
        "ddd4j_version": resolve(properties.get("ddd4j.version"), properties),
        "java_version": java_version,
        "spring_boot_parent": {
            "group_id": parent_group,
            "artifact_id": parent_artifact,
            "version": parent_version,
        },
        "spring_boot_version": spring_boot_version,
        "root_module_kind": root_module_kind,
        "root_modules": modules,
    }


def count_entries(content: str) -> int:
    normalized = re.sub(r"\\\r?\n", "", content)
    values: list[str] = []
    for line in normalized.splitlines():
        line = line.strip()
        if not line or line.startswith("#") or line.startswith("!"):
            continue
        values.extend(part.strip() for part in line.split("=", 1)[-1].split(",") if part.strip())
    return len(values)


def registration_inventory(paths: list[str], read_content: callable) -> dict[str, object]:
    factories = [path for path in paths if path.endswith("META-INF/spring.factories")]
    imports = [path for path in paths if path.endswith("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports")]
    return {
        "spring_factories_files": factories,
        "spring_factories_entry_count": sum(count_entries(read_content(path)) for path in factories),
        "auto_configuration_import_files": imports,
        "auto_configuration_import_entry_count": sum(count_entries(read_content(path)) for path in imports),
    }


def read_tsv(path: Path) -> list[dict[str, str]]:
    with path.open(encoding="utf-8", newline="") as source:
        reader = csv.DictReader(source, delimiter="\t")
        if reader.fieldnames != BRANCH_COLUMNS:
            fail(f"invalid branch table header: {path}")
        return [{key: value.strip() for key, value in row.items()} for row in reader]


def git(repo: Path, *arguments: str) -> str:
    result = subprocess.run(["git", "-C", str(repo), *arguments], text=True, capture_output=True, check=False)
    if result.returncode != 0:
        fail(f"git {' '.join(arguments)} failed: {result.stderr.strip()}")
    return result.stdout


def inspect_tree(pom: Path, tree: Path) -> dict[str, object]:
    if not pom.is_file():
        fail(f"missing POM file: {pom}")
    parsed = parse_pom(pom.read_text(encoding="utf-8"), str(pom))
    paths = [item.relative_to(tree).as_posix() for item in tree.rglob("*") if item.is_file()]
    return {**parsed, **registration_inventory(paths, lambda path: (tree / path).read_text(encoding="utf-8"))}


def inspect_branch(repo: Path, branch: dict[str, str], ref_prefix: str) -> dict[str, object]:
    name = branch["branch"]
    ref = f"{ref_prefix}{name}"
    sha = git(repo, "rev-parse", ref).strip()
    pom = git(repo, "show", f"{ref}:pom.xml")
    paths = git(repo, "ls-tree", "-r", "--name-only", ref).splitlines()
    parsed = parse_pom(pom, f"{ref}:pom.xml")
    inventory = registration_inventory(paths, lambda path: git(repo, "show", f"{ref}:{path}"))
    return {"branch": name, "source_ref": ref, "commit": sha, **branch, **parsed, **inventory}


def write_json(path: Path, value: object) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--repo", type=Path)
    parser.add_argument("--branches", type=Path)
    parser.add_argument("--ref-prefix", default="origin/")
    parser.add_argument("--inspect-pom", type=Path)
    parser.add_argument("--tree", type=Path)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    try:
        if args.inspect_pom:
            if args.repo or args.branches or not args.tree:
                fail("--inspect-pom requires --tree and cannot be combined with --repo/--branches")
            write_json(args.output, inspect_tree(args.inspect_pom, args.tree))
        else:
            if not args.repo or not args.branches or args.tree:
                fail("--repo and --branches are required for a Git branch audit")
            branches = read_tsv(args.branches)
            records = [inspect_branch(args.repo, branch, args.ref_prefix) for branch in branches]
            write_json(args.output, {"branch_count": len(records), "branches": records})
    except ValueError as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
