#!/usr/bin/env python3
"""Verify Maven Model 4.1 parent declarations in a reactor."""

import argparse
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


def child(parent, name):
    return parent.find(f"{{*}}{name}")


def value(parent, name):
    element = child(parent, name)
    return (element.text or "").strip() if element is not None else ""


def artifact_id(pom):
    if not pom.is_file():
        return ""
    project = ET.parse(pom).getroot()
    return value(project, "artifactId")


def load_allowlist(path):
    if path is None:
        return set()
    entries = set()
    for line in path.read_text(encoding="utf-8").splitlines():
        if not line or line.startswith("#") or line.startswith("path\t"):
            continue
        entries.add(line.split("\t", 1)[0])
    return entries


def verify(root, allowed_default_mismatches=None):
    root = Path(root)
    errors = []
    allowed = set(allowed_default_mismatches or ())
    observed_mismatches = set()
    models = [(pom, ET.parse(pom).getroot()) for pom in sorted(root.rglob("pom.xml"))]
    reactor_projects = {value(project, "artifactId") for _, project in models}
    for pom, project in models:
        if value(project, "modelVersion") != "4.1.0":
            continue
        parent = child(project, "parent")
        if parent is None:
            continue

        relative_element = child(parent, "relativePath")
        coordinates = tuple(value(parent, name) for name in ("groupId", "artifactId", "version"))
        _, parent_artifact, _ = coordinates
        if relative_element is not None:
            errors.append(f"{pom}: parent must not declare relativePath")
        if not all(coordinates):
            errors.append(f"{pom}: parent must use full coordinates")
        default_parent_artifact = artifact_id(pom.parent.parent / "pom.xml")
        if parent_artifact in reactor_projects and default_parent_artifact != parent_artifact:
            relative = pom.relative_to(root).as_posix()
            observed_mismatches.add(relative)
            if relative not in allowed:
                errors.append(f"{pom}: default parent path mismatch is not allowlisted")
    for relative in sorted(allowed - observed_mismatches):
        errors.append(f"{relative}: allowlist entry no longer matches a default parent path mismatch")
    return errors


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("root", type=Path)
    parser.add_argument("--allowlist", type=Path)
    args = parser.parse_args()
    errors = verify(args.root, load_allowlist(args.allowlist))
    if errors:
        print("\n".join(errors))
        return 1
    print(f"PASS: Maven Model 4.1 parent contracts under {args.root}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
