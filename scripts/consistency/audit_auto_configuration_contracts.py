#!/usr/bin/env python3
"""Inventory Spring Boot auto-configuration registrations across maintenance refs."""

from __future__ import annotations

import argparse
import csv
import json
import subprocess
import sys
from collections import defaultdict
from pathlib import Path


BRANCH_COLUMNS = ["branch", "jdk_group", "boot_line", "ddd4j_line", "registration_mode"]
CONTRACT_COLUMNS = ["contract_id", "capability", "observable_behavior", "required_state", "owner"]
FACTORY_KEY = "org.springframework.boot.autoconfigure.EnableAutoConfiguration"


def fail(message: str) -> None:
    raise ValueError(message)


def read_tsv(path: Path, columns: list[str]) -> list[dict[str, str]]:
    with path.open(encoding="utf-8", newline="") as source:
        reader = csv.DictReader(source, delimiter="\t")
        if reader.fieldnames != columns:
            fail(f"invalid TSV header: {path}")
        return [{key: value.strip() for key, value in row.items()} for row in reader]


def registration_entries(path: str, content: str) -> list[str]:
    if path.endswith("AutoConfiguration.imports"):
        return [line.strip() for line in content.splitlines() if line.strip() and not line.lstrip().startswith("#")]
    normalized = content.replace("\\\n", "")
    entries: list[str] = []
    for line in normalized.splitlines():
        if not line.strip() or line.lstrip().startswith(("#", "!")) or "=" not in line:
            continue
        key, value = line.split("=", 1)
        if key.strip() == FACTORY_KEY:
            entries.extend(value.strip() for value in value.split(",") if value.strip())
    return entries


def contract_for(class_name: str, file_path: str, contract_ids: set[str]) -> str | None:
    value = f"{class_name} {file_path}".lower()
    patterns = (
        ("WEBMVC_WEBFLUX", ("webmvc", "webflux", "web/", "web-")),
        ("AUTH", ("satoken", "security", "shiro", "license", "auth")),
        ("DATA_CACHE", ("mybatis", "cache", "crypto", "datascope", "external", "logs", "jpa", "data")),
        ("MQ", ("kafka", "rabbit", "rocket", "mqtt", "pulsar", "nats", "activemq", "redis", "mq", "sqs", "tdmq")),
        ("CORE_SPI_REPOSITORY_CQRS", ("core", "repository", "command", "cqrs", "runtime")),
        ("AUTO_CONFIGURATION", ("autoconfiguration", "auto_configuration")),
    )
    for contract_id, tokens in patterns:
        if contract_id in contract_ids and any(token in value for token in tokens):
            return contract_id
    return None


def build_inventory(paths: list[str], read_content: callable, expected: set[str] | None, contract_ids: set[str]) -> dict[str, object]:
    registrations: dict[str, set[str]] = defaultdict(set)
    files_by_class: dict[str, set[str]] = defaultdict(set)
    registration_paths = [
        path for path in paths
        if path.endswith("META-INF/spring.factories") or path.endswith("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports")
    ]
    for path in registration_paths:
        source = "imports" if path.endswith("AutoConfiguration.imports") else "factories"
        for class_name in registration_entries(path, read_content(path)):
            registrations[class_name].add(source)
            files_by_class[class_name].add(path)
    all_classes = sorted(set(registrations) | (expected or set()))
    entries = []
    unmapped = 0
    duplicates = 0
    for class_name in all_classes:
        sources = registrations.get(class_name, set())
        if not sources:
            status = "MISSING"
        elif sources == {"imports"}:
            status = "AUTO_CONFIGURATION"
        elif sources == {"factories"}:
            status = "LEGACY_FACTORY"
        else:
            status = "DUPLICATE_REGISTRATION"
            duplicates += 1
        files = sorted(files_by_class.get(class_name, set()))
        contract_id = contract_for(class_name, " ".join(files), contract_ids) if sources else None
        mapping_status = "MAPPED" if contract_id else "UNMAPPED"
        if mapping_status == "UNMAPPED" and sources:
            unmapped += 1
        entries.append({
            "class_name": class_name,
            "status": status,
            "registration_files": files,
            "contract_id": contract_id,
            "mapping_status": mapping_status,
        })
    return {
        "registration_file_count": len(registration_paths),
        "registrations": entries,
        "unmapped_registration_count": unmapped,
        "duplicate_registration_count": duplicates,
    }


def git(repo: Path, *arguments: str) -> str:
    result = subprocess.run(["git", "-C", str(repo), *arguments], text=True, capture_output=True, check=False)
    if result.returncode != 0:
        fail(f"git {' '.join(arguments)} failed: {result.stderr.strip()}")
    return result.stdout


def write_json(path: Path, value: object) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--inspect-tree", type=Path)
    parser.add_argument("--expected", type=Path)
    parser.add_argument("--repo", type=Path)
    parser.add_argument("--branches", type=Path)
    parser.add_argument("--contracts", type=Path)
    parser.add_argument("--ref-prefix", default="origin/")
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    try:
        if args.inspect_tree:
            if args.repo or args.branches or args.contracts or not args.expected:
                fail("--inspect-tree requires --expected and cannot be combined with Git audit inputs")
            paths = [item.relative_to(args.inspect_tree).as_posix() for item in args.inspect_tree.rglob("*") if item.is_file()]
            expected = {line.strip() for line in args.expected.read_text(encoding="utf-8").splitlines() if line.strip()}
            inventory = build_inventory(paths, lambda path: (args.inspect_tree / path).read_text(encoding="utf-8"), expected, set())
            write_json(args.output, inventory)
            return 0
        if not args.repo or not args.branches or not args.contracts or args.expected:
            fail("Git audit requires --repo, --branches and --contracts")
        branches = read_tsv(args.branches, BRANCH_COLUMNS)
        contracts = read_tsv(args.contracts, CONTRACT_COLUMNS)
        contract_ids = {row["contract_id"] for row in contracts}
        records = []
        has_findings = False
        for branch in branches:
            ref = f"{args.ref_prefix}{branch['branch']}"
            paths = git(args.repo, "ls-tree", "-r", "--name-only", ref).splitlines()
            inventory = build_inventory(paths, lambda path: git(args.repo, "show", f"{ref}:{path}"), None, contract_ids)
            has_findings = has_findings or bool(inventory["unmapped_registration_count"] or inventory["duplicate_registration_count"])
            records.append({"branch": branch["branch"], "source_ref": ref, **inventory})
        write_json(args.output, {"branch_count": len(records), "audit_status": "FAIL" if has_findings else "PASS", "branches": records})
        return 2 if has_findings else 0
    except (OSError, ValueError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
