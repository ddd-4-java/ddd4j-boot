#!/usr/bin/env python3
"""Validate the ddd4j-boot release-line handoff matrix."""

from __future__ import annotations

import argparse
import csv
import re
import subprocess
import sys
from pathlib import Path


INPUT_COLUMNS = ["branch", "github_sha", "codeup_sha", "java", "spring_boot", "ddd4j",
                 "logical_state", "report_ref"]
SHA = re.compile(r"^[0-9a-f]{40}$")


def rows(path: Path) -> list[dict[str, str]]:
    with path.open(encoding="utf-8", newline="") as source:
        reader = csv.DictReader(source, delimiter="\t")
        if reader.fieldnames != INPUT_COLUMNS:
            raise ValueError(f"invalid input header: {reader.fieldnames}")
        return [{key: (value or "").strip() for key, value in row.items()} for row in reader]


def matrix(path: Path) -> dict[str, dict[str, str]]:
    with path.open(encoding="utf-8", newline="") as source:
        return {row["branch"]: row for row in csv.DictReader(source, delimiter="\t")}


def command(repository: Path, *args: str) -> tuple[int, str]:
    result = subprocess.run(
        ["git", "-C", str(repository), *args], capture_output=True, text=True, check=False
    )
    return result.returncode, result.stdout.strip()


def verify(matrix_path: Path, input_path: Path, repository: Path | None = None,
           evidence_dir: Path | None = None, verify_git: bool = False) -> list[str]:
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
        if row["logical_state"] != "PASS":
            errors.append(f"{branch}: logical_state must be PASS")
        if not row["report_ref"]:
            errors.append(f"{branch}: report_ref is required")
        elif repository is not None:
            report = repository / row["report_ref"]
            if not report.is_file():
                errors.append(f"{branch}: report_ref does not exist")
            else:
                content = report.read_text(encoding="utf-8")
                if row["github_sha"][:12] not in content or "clean verify" not in content:
                    errors.append(f"{branch}: report_ref is not bound to verified SHA and clean verify")
        if evidence_dir is not None:
            log = evidence_dir / f"ddd4j-boot-{branch}-clean-verify-final.log"
            content = log.read_text(encoding="utf-8", errors="replace") if log.is_file() else ""
            if ("BUILD SUCCESS" not in content or
                    f"Reactor Summary for ddd4j-boot {branch}.20260630-SNAPSHOT" not in content):
                errors.append(f"{branch}: clean verify evidence log is missing or incomplete")
        if verify_git and repository is not None and SHA.fullmatch(row["github_sha"]):
            code, _ = command(repository, "cat-file", "-e", row["github_sha"] + "^{commit}")
            if code:
                errors.append(f"{branch}: verified SHA is not a local commit")
            for remote, field in (("github", "github_sha"), ("origin", "codeup_sha")):
                code, output = command(repository, "ls-remote", remote, f"refs/heads/{branch}")
                remote_sha = output.split("\t", 1)[0] if code == 0 and output else ""
                if not SHA.fullmatch(remote_sha):
                    errors.append(f"{branch}: cannot resolve {remote} head")
                    continue
                code, _ = command(repository, "merge-base", "--is-ancestor", row[field], remote_sha)
                if code:
                    errors.append(f"{branch}: verified SHA is not an ancestor of {remote} head")
    return errors


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--matrix", type=Path, required=True)
    parser.add_argument("--input", type=Path, required=True)
    parser.add_argument("--repository", type=Path, default=Path("."))
    parser.add_argument("--evidence-dir", type=Path, required=True)
    parser.add_argument("--skip-git", action="store_true")
    args = parser.parse_args()
    try:
        errors = verify(args.matrix, args.input, args.repository, args.evidence_dir,
                        verify_git=not args.skip_git)
    except ValueError as error:
        errors = [str(error)]
    if errors:
        print("\n".join("ERROR: " + error for error in errors), file=sys.stderr)
        return 1
    print(f"PASS: {len(rows(args.input))} release lines are complete")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
