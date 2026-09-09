#!/usr/bin/env python3
"""Validate the ddd4j-boot release-line handoff matrix."""

from __future__ import annotations

import argparse
import csv
import re
import sys
from pathlib import Path


INPUT_COLUMNS = ["branch", "github_sha", "codeup_sha", "java", "spring_boot", "ddd4j",
                 "logical_state", "report_ref"]
SHA = re.compile(r"^[0-9a-f]{40}$")
STATES = {"PASS", "BLOCKED", "NOT_APPLICABLE"}


def rows(path: Path) -> list[dict[str, str]]:
    with path.open(encoding="utf-8", newline="") as source:
        reader = csv.DictReader(source, delimiter="\t")
        if reader.fieldnames != INPUT_COLUMNS:
            raise ValueError(f"invalid input header: {reader.fieldnames}")
        return [{key: (value or "").strip() for key, value in row.items()} for row in reader]


def matrix(path: Path) -> dict[str, dict[str, str]]:
    with path.open(encoding="utf-8", newline="") as source:
        return {row["branch"]: row for row in csv.DictReader(source, delimiter="\t")}


def verify(matrix_path: Path, input_path: Path) -> list[str]:
    expected = matrix(matrix_path)
    actual_rows = rows(input_path)
    actual = {row["branch"]: row for row in actual_rows}
    errors = []
    if len(actual) != len(actual_rows):
        errors.append("duplicate branch in release input")
    missing = sorted(set(expected) - set(actual))
    extra = sorted(set(actual) - set(expected))
    if missing:
        errors.append("missing release lines: " + ", ".join(missing))
    if extra:
        errors.append("unexpected release lines: " + ", ".join(extra))
    for branch in sorted(set(expected) & set(actual)):
        row, contract = actual[branch], expected[branch]
        for remote in ("github_sha", "codeup_sha"):
            if not SHA.fullmatch(row[remote]):
                errors.append(f"{branch}: invalid {remote}")
        if row["github_sha"] != row["codeup_sha"]:
            errors.append(f"{branch}: GitHub and Codeup SHA differ")
        checks = (("java", "jdk"), ("spring_boot", "boot_version"), ("ddd4j", "ddd4j_version"))
        for field, matrix_field in checks:
            if row[field] != contract[matrix_field]:
                errors.append(f"{branch}: {field} {row[field]} != matrix {contract[matrix_field]}")
        if row["logical_state"] not in STATES:
            errors.append(f"{branch}: invalid logical_state {row['logical_state']}")
        if not row["report_ref"]:
            errors.append(f"{branch}: report_ref is required")
    return errors


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--matrix", type=Path, required=True)
    parser.add_argument("--input", type=Path, required=True)
    args = parser.parse_args()
    try:
        errors = verify(args.matrix, args.input)
    except ValueError as error:
        errors = [str(error)]
    if errors:
        print("\n".join("ERROR: " + error for error in errors), file=sys.stderr)
        return 1
    print(f"PASS: {len(rows(args.input))} release lines are complete")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
