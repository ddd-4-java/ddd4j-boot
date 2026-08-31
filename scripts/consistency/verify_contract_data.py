#!/usr/bin/env python3
"""Validate the ddd4j-boot cross-JDK logical-contract input tables."""

from __future__ import annotations

import argparse
import csv
import sys
from pathlib import Path


EXPECTED_BRANCHES = {
    "2.3.x", "2.4.x", "2.5.x", "2.6.x", "2.7.x", "3.0.x", "3.1.x",
    "3.2.x", "3.3.x", "3.4.x", "3.5.x", "4.0.x", "4.1.x",
}
BRANCH_COLUMNS = ["branch", "jdk_group", "boot_line", "ddd4j_line", "registration_mode"]
CONTRACT_COLUMNS = ["contract_id", "capability", "observable_behavior", "required_state", "owner"]
EXCEPTION_COLUMNS = ["branch", "contract_id", "status", "reason", "migration_document", "evidence_ref"]
STATES = {"REQUIRED", "ADAPTED", "NOT_APPLICABLE", "BLOCKED"}


def fail(message: str) -> None:
    raise ValueError(message)


def read_tsv(path: Path, columns: list[str]) -> list[dict[str, str]]:
    if not path.is_file():
        fail(f"missing TSV file: {path}")
    with path.open(encoding="utf-8", newline="") as source:
        reader = csv.DictReader(source, delimiter="\t")
        if reader.fieldnames != columns:
            fail(f"invalid header in {path}: expected {'|'.join(columns)}")
        rows = []
        for line, row in enumerate(reader, start=2):
            if None in row or any(value is None for value in row.values()):
                fail(f"invalid column count in {path}:{line}")
            rows.append({key: value.strip() for key, value in row.items()})
        return rows


def non_blank(row: dict[str, str], field: str, table: str) -> None:
    if not row[field]:
        fail(f"{table} has blank {field}")


def validate(branches_path: Path, contracts_path: Path, exceptions_path: Path) -> None:
    branches = read_tsv(branches_path, BRANCH_COLUMNS)
    branch_names = [row["branch"] for row in branches]
    if len(branch_names) != len(set(branch_names)):
        fail("branch table has duplicate branch")
    if set(branch_names) != EXPECTED_BRANCHES:
        missing = sorted(EXPECTED_BRANCHES - set(branch_names))
        extra = sorted(set(branch_names) - EXPECTED_BRANCHES)
        fail(f"branch table must contain exactly 13 maintenance branches; missing={missing}, extra={extra}")
    for row in branches:
        for field in BRANCH_COLUMNS:
            non_blank(row, field, "branch table")

    contracts = read_tsv(contracts_path, CONTRACT_COLUMNS)
    contract_ids = [row["contract_id"] for row in contracts]
    if not contract_ids:
        fail("contract table has no contracts")
    if len(contract_ids) != len(set(contract_ids)):
        fail("contract table has duplicate contract_id")
    for row in contracts:
        for field in CONTRACT_COLUMNS:
            non_blank(row, field, "contract table")
        if row["required_state"] not in STATES:
            fail(f"unsupported required_state: {row['required_state']}")

    exceptions = read_tsv(exceptions_path, EXCEPTION_COLUMNS)
    seen_exceptions: set[tuple[str, str]] = set()
    for row in exceptions:
        key = (row["branch"], row["contract_id"])
        if key in seen_exceptions:
            fail(f"exception table has duplicate branch/contract: {key[0]}/{key[1]}")
        seen_exceptions.add(key)
        if row["branch"] not in EXPECTED_BRANCHES:
            fail(f"exception table references unknown branch: {row['branch']}")
        if row["contract_id"] not in set(contract_ids):
            fail(f"exception table references unknown contract: {row['contract_id']}")
        if row["status"] not in STATES:
            fail(f"unsupported status: {row['status']}")
        if row["status"] in {"BLOCKED", "NOT_APPLICABLE"} and not row["reason"]:
            fail(f"{row['status']} requires a reason for {row['branch']}/{row['contract_id']}")
        if row["status"] == "NOT_APPLICABLE":
            migration = row["migration_document"]
            if not migration.startswith("docs/migration/"):
                fail(f"NOT_APPLICABLE requires migration document for {row['branch']}/{row['contract_id']}")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--branches", type=Path, required=True)
    parser.add_argument("--contracts", type=Path, required=True)
    parser.add_argument("--exceptions", type=Path, required=True)
    args = parser.parse_args()
    try:
        validate(args.branches, args.contracts, args.exceptions)
    except ValueError as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1
    print("PASS: 13 maintenance branches and logical-contract metadata are valid")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
