#!/usr/bin/env python3
"""Generate public Java and Spring configuration surfaces from a source tree."""

from __future__ import annotations

import argparse
import csv
import json
import re
from pathlib import Path


PACKAGE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)
TYPE = re.compile(r"\bpublic\s+(?:abstract\s+|final\s+|sealed\s+)?(?:class|interface|enum|record)\s+(\w+)")
METHOD = re.compile(
    r"^\s*(public|protected)\s+(?:(?:static|final|abstract|synchronized|default)\s+)*"
    r"([\w.$<>?, \[\]]+)\s+(\w+)\s*\(([^)]*)\)",
    re.MULTILINE,
)
ANNOTATION = re.compile(r"@\w+(?:\([^)]*\))?\s*")


def parameter_types(parameters: str) -> str:
    if not parameters.strip():
        return ""
    result = []
    for parameter in parameters.split(","):
        value = ANNOTATION.sub("", parameter).replace("final ", "").strip()
        parts = value.split()
        result.append(" ".join(parts[:-1]) if len(parts) > 1 else value)
    return ",".join(result)


def java_surfaces(path: Path, group: str) -> list[tuple[str, str, str, str]]:
    content = path.read_text(encoding="utf-8")
    package_match = PACKAGE.search(content)
    type_match = TYPE.search(content)
    if not package_match or not type_match:
        return []
    qualified = package_match.group(1) + "." + type_match.group(1)
    rows = [(group, "type:" + qualified, "present", "false")]
    for match in METHOD.finditer(content):
        preceding = content[max(0, match.start() - 160):match.start()]
        deprecated = "true" if "@Deprecated" in preceding else "false"
        return_type = " ".join(match.group(2).split())
        signature = f"method:{qualified}#{match.group(3)}({parameter_types(match.group(4))})"
        rows.append((group, signature, return_type, deprecated))
    return rows


def metadata_surfaces(path: Path, group: str) -> list[tuple[str, str, str, str]]:
    try:
        document = json.loads(path.read_text(encoding="utf-8"))
    except (json.JSONDecodeError, UnicodeDecodeError):
        return []
    rows = []
    for item in document.get("properties", []):
        name = item.get("name")
        if not name:
            continue
        value = str(item.get("type", ""))
        if "defaultValue" in item:
            value += "=" + json.dumps(item["defaultValue"], ensure_ascii=False, separators=(",", ":"))
        rows.append((group, "config:" + name, value, "false"))
    return rows


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--tree", type=Path, required=True)
    parser.add_argument("--jdk-group", required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    rows = []
    for source in sorted(args.tree.rglob("src/main/java/**/*.java")):
        if "ddd4j-boot-samples" in source.parts:
            continue
        rows.extend(java_surfaces(source, args.jdk_group))
    for metadata in sorted(args.tree.rglob("spring-configuration-metadata.json")):
        if "ddd4j-boot-samples" in metadata.parts:
            continue
        rows.extend(metadata_surfaces(metadata, args.jdk_group))

    unique = sorted(set(rows), key=lambda row: (row[0], row[1], row[2], row[3]))
    args.output.parent.mkdir(parents=True, exist_ok=True)
    with args.output.open("w", encoding="utf-8", newline="") as target:
        writer = csv.writer(target, delimiter="\t", lineterminator="\n")
        writer.writerow(("jdk_group", "surface", "value", "deprecated"))
        writer.writerows(unique)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
