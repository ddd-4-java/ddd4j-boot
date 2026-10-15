#!/usr/bin/env python3
"""Render branch/contract evidence without inferring PASS from structural scans."""

from __future__ import annotations

import argparse
import csv
import json
import sys
from pathlib import Path


def rows(path: Path) -> list[dict[str, str]]:
    with path.open(encoding="utf-8", newline="") as source:
        return [{key: value.strip() for key, value in row.items()} for row in csv.DictReader(source, delimiter="\t")]


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--branches", type=Path, required=True)
    parser.add_argument("--contracts", type=Path, required=True)
    parser.add_argument("--exceptions", type=Path, required=True)
    parser.add_argument("--baseline", type=Path, required=True)
    parser.add_argument("--auto-config", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    branches, contracts, exceptions = rows(args.branches), rows(args.contracts), rows(args.exceptions)
    baseline = {item["branch"]: item for item in json.loads(args.baseline.read_text(encoding="utf-8"))["branches"]}
    auto_config = {item["branch"]: item for item in json.loads(args.auto_config.read_text(encoding="utf-8"))["branches"]}
    exception_map = {(item["branch"], item["contract_id"]): item for item in exceptions}
    rendered, findings = [], False
    for branch in branches:
        name = branch["branch"]
        for contract in contracts:
            item = exception_map.get((name, contract["contract_id"]), {})
            requested = item.get("status", contract["required_state"])
            reason, migration, evidence = item.get("reason", ""), item.get("migration_document", ""), item.get("evidence_ref", "")
            if requested == "BLOCKED" and not reason:
                state, reason = "INVALID_EVIDENCE", "BLOCKED requires a reason"
            elif requested == "NOT_APPLICABLE" and (not reason or not migration):
                state, reason = "INVALID_EVIDENCE", "NOT_APPLICABLE requires reason and migration document"
            elif requested in {"REQUIRED", "ADAPTED"} and not evidence:
                state, reason = "MISSING_EVIDENCE", "No executed contract test or reviewed evidence reference"
            elif requested in {"REQUIRED", "ADAPTED"}:
                state = "PASS"
            else:
                state = requested
            findings = findings or state != "PASS"
            rendered.append((name, contract["contract_id"], state, evidence or reason or "-"))
    lines = ["# ddd4j-boot 逻辑一致性证据", "", "本报告不会把 POM 或注册扫描自动视为行为通过。", "", "| 分支 | 契约 | 状态 | 证据/原因 |", "|---|---|---|---|"]
    lines.extend(f"| {branch} | {contract} | {state} | {evidence} |" for branch, contract, state, evidence in rendered)
    lines.extend(["", "## 结构审计", ""])
    for branch in branches:
        name = branch["branch"]
        base, auto = baseline.get(name, {}), auto_config.get(name, {})
        lines.append(f"- `{name}`: SHA `{base.get('commit', 'MISSING')}`, Java `{base.get('java_version', 'MISSING')}`, Boot `{base.get('spring_boot_version', 'MISSING')}`, auto-registration unmapped `{auto.get('unmapped_registration_count', 'MISSING')}`, duplicate `{auto.get('duplicate_registration_count', 'MISSING')}`.")
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text("\n".join(lines) + "\n", encoding="utf-8")
    return 2 if findings else 0


if __name__ == "__main__":
    raise SystemExit(main())
