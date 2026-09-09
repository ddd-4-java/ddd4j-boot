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


def parent_target(pom, relative_path):
    target = (pom.parent / relative_path).resolve()
    return target / "pom.xml" if target.is_dir() else target


def verify(root):
    root = Path(root)
    errors = []
    for pom in sorted(root.rglob("pom.xml")):
        project = ET.parse(pom).getroot()
        if value(project, "modelVersion") != "4.1.0":
            continue
        parent = child(project, "parent")
        if parent is None:
            continue

        relative_element = child(parent, "relativePath")
        relative_path = value(parent, "relativePath")
        coordinates = tuple(value(parent, name) for name in ("groupId", "artifactId", "version"))

        if relative_element is None:
            if not all(coordinates):
                errors.append(f"{pom}: external parent must use coordinates only")
            continue
        if not relative_path:
            errors.append(f"{pom}: external parent must use coordinates only")
        elif any(coordinates):
            errors.append(f"{pom}: reactor parent must use relativePath only")
        elif not parent_target(pom, relative_path).is_file():
            errors.append(f"{pom}: relativePath does not exist: {relative_path}")
    return errors


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("root", type=Path)
    args = parser.parse_args()
    errors = verify(args.root)
    if errors:
        print("\n".join(errors))
        return 1
    print(f"PASS: Maven Model 4.1 parent contracts under {args.root}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
