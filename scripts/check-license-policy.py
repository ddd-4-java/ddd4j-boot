#!/usr/bin/env python3
"""Check component licenses against the resolved, nonempty release SBOM."""

import argparse
import json
import re
from pathlib import Path


COMPONENT = re.compile(r"^\s*\* .*\(([A-Za-z0-9_.-]+:[A-Za-z0-9_.-]+:[^\s()]+) - ")
HEADING = re.compile(r"^    (\S.*):\s*$")
FORBIDDEN = re.compile(
    r"AFFERO|AGPL|GENERAL PUBLIC LICENSE|\bGPL\b|LGPL|LESSER GENERAL|CDDL|Commercial License|Unknown [Ll]icense",
    re.IGNORECASE,
)
ALLOWED = re.compile(
    r"Apache(?:[- ]2\.0| (?:Software )?License,? Version 2\.0)|"
    r"\bApache License v?2\.0\b|\bAL 2\.0\b|"
    r"Eclipse Public License(?: v\.?| - Version)? ?2\.0|"
    r"\bMIT\b|\bBSD\b|\bEPL[- ]2\.0\b|\bISC\b",
    re.IGNORECASE,
)


def read_table(path):
    if not path.is_file():
        return {}
    result = {}
    for number, line in enumerate(path.read_text(encoding="utf-8").splitlines(), 1):
        if not line.strip() or line.lstrip().startswith("#"):
            continue
        fields = line.split("\t", 1)
        if len(fields) != 2 or fields[0] in result:
            raise ValueError(f"Invalid or duplicate policy row {path}:{number}")
        result[fields[0]] = fields[1].strip()
    return result


def report_licenses(path):
    current = None
    result = {}
    for line in path.read_text(encoding="utf-8").splitlines():
        heading = HEADING.match(line)
        if heading:
            current = heading.group(1)
            continue
        component = COMPONENT.match(line)
        if component:
            if current is None:
                raise ValueError(f"License report component lacks heading: {line}")
            result.setdefault(component.group(1), set()).add(current)
    if not result:
        raise ValueError("License report has no components")
    return result


def sbom_components(path):
    bom = json.loads(path.read_text(encoding="utf-8"))
    components = bom.get("components") or []
    graph = bom.get("dependencies") or []
    if bom.get("bomFormat") != "CycloneDX" or not components or not graph:
        raise ValueError("Release SBOM is empty or lacks a dependency graph")
    result = set()
    refs = set()
    for component in components:
        fields = [component.get(key) for key in ("group", "name", "version")]
        ref = component.get("bom-ref")
        if not all(fields) or not ref or ref in refs:
            raise ValueError("Release SBOM component is incomplete or duplicated")
        refs.add(ref)
        result.add(":".join(fields))
    if any(item.get("ref") not in refs or
           any(child not in refs for child in item.get("dependsOn", []))
           for item in graph):
        raise ValueError("Release SBOM has disconnected dependency references")
    return result


def check(report, sbom, selections, exclusions):
    licenses = report_licenses(report)
    included = sbom_components(sbom)
    chosen = read_table(selections)
    build_only = read_table(exclusions)
    errors = []
    passes = []
    for coordinate, names in sorted(licenses.items()):
        blocked = sorted(name for name in names if FORBIDDEN.search(name))
        if not blocked:
            continue
        selection = chosen.get(coordinate)
        if selection is not None:
            if selection in names and ALLOWED.search(selection) and not FORBIDDEN.search(selection):
                passes.append(f"Selected {selection} for {coordinate}")
                continue
            errors.append(f"{coordinate}: selected license is absent or not allowed: {selection}")
            continue
        if coordinate in build_only and coordinate not in included:
            passes.append(f"Excluded build-tool component absent from SBOM: {coordinate}")
            continue
        errors.append(f"{coordinate}: {', '.join(blocked)}")
    return passes, errors


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--report", required=True, type=Path)
    parser.add_argument("--sbom", required=True, type=Path)
    parser.add_argument("--selections", required=True, type=Path)
    parser.add_argument("--build-tool-exclusions", required=True, type=Path)
    parser.add_argument("--violations", required=True, type=Path)
    args = parser.parse_args()
    passes, errors = check(args.report, args.sbom, args.selections, args.build_tool_exclusions)
    for line in passes:
        print("[PASS] " + line)
    args.violations.write_text("\n".join(errors) + ("\n" if errors else ""), encoding="utf-8")
    if errors:
        for line in errors:
            print("[FAIL] " + line)
        raise SystemExit(1)
    print("[PASS] No blocked component license is present in the release SBOM")


if __name__ == "__main__":
    main()
