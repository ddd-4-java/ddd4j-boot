#!/usr/bin/env python3
"""Validate cross-JDK migrations and reject undocumented same-group surface drift."""

from __future__ import annotations

import argparse
import csv
import json
import sys
from pathlib import Path


MAP_COLUMNS = ["from_group", "to_group", "contract_id", "source_surface", "target_surface", "behavior_delta", "validation_command"]
SURFACE_COLUMNS = ["jdk_group", "surface", "value", "deprecated"]


def fail(message: str) -> None:
    raise ValueError(message)


def read_tsv(path: Path, columns: list[str]) -> list[dict[str, str]]:
    with path.open(encoding="utf-8", newline="") as source:
        reader = csv.DictReader(source, delimiter="\t")
        if reader.fieldnames != columns:
            fail(f"invalid header in {path}")
        return [{key: value.strip() for key, value in row.items()} for row in reader]


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--migration-map", type=Path, required=True)
    parser.add_argument("--baseline-surfaces", type=Path, required=True)
    parser.add_argument("--candidate-surfaces", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    try:
        mappings = read_tsv(args.migration_map, MAP_COLUMNS)
        for mapping in mappings:
            if mapping["from_group"] == mapping["to_group"]:
                fail(f"migration must cross JDK groups: {mapping['contract_id']}")
            for field in ("contract_id", "source_surface", "target_surface", "behavior_delta", "validation_command"):
                if not mapping[field]:
                    fail(f"cross-group mapping requires {field}: {mapping['from_group']}->{mapping['to_group']}")
        baseline = {(row["jdk_group"], row["surface"]): row for row in read_tsv(args.baseline_surfaces, SURFACE_COLUMNS)}
        candidate = {(row["jdk_group"], row["surface"]): row for row in read_tsv(args.candidate_surfaces, SURFACE_COLUMNS)}
        drift = []
        for key in sorted(set(baseline) & set(candidate)):
            before, after = baseline[key], candidate[key]
            if before["value"] != after["value"] and after["deprecated"].lower() != "true":
                drift.append({"jdk_group": key[0], "surface": key[1], "before": before["value"], "after": after["value"]})
        if drift:
            fail("undocumented same-group drift: " + ", ".join(f"{item['jdk_group']}/{item['surface']}" for item in drift))
        result = {"migration_count": len(mappings), "migrations": [{**mapping, "status": "ADAPTED"} for mapping in mappings], "same_group_drift": drift, "status": "PASS"}
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(result, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    except ValueError as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
