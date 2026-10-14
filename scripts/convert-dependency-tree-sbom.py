#!/usr/bin/env python3
"""Build and validate a CycloneDX BOM from Maven's resolved dependency trees."""

import argparse
import datetime
import json
from pathlib import Path
import sys
import urllib.parse
import uuid
import xml.etree.ElementTree as ET


def fail(message):
    raise SystemExit(message)


def local_name(tag):
    return tag.rsplit("}", 1)[-1]


def reactor_modules(root):
    visited = set()
    result = []

    def walk(directory):
        pom = directory / "pom.xml"
        if not pom.is_file():
            fail("Missing reactor POM: " + str(pom))
        model = ET.parse(pom).getroot()
        for container in model:
            if local_name(container.tag) not in ("modules", "subprojects"):
                continue
            for element in container:
                if local_name(element.tag) not in ("module", "subproject"):
                    continue
                if not element.text or not element.text.strip():
                    fail("Blank reactor module in " + str(pom))
                child = (directory / element.text.strip()).resolve()
                try:
                    relative = child.relative_to(root)
                except ValueError:
                    fail("Reactor module escapes checkout: " + str(child))
                if relative.parts and relative.parts[0] == "ddd4j-boot-samples":
                    continue
                if child in visited:
                    continue
                visited.add(child)
                result.append(relative.as_posix())
                walk(child)

    walk(root)
    if not result:
        fail("No publishable reactor modules were found")
    return result


def coordinate(node):
    for key in ("groupId", "artifactId", "version"):
        if not node.get(key):
            fail("Resolved dependency tree node lacks " + key + ": " + repr(node))
    group = urllib.parse.quote(node["groupId"], safe=".-_")
    artifact = urllib.parse.quote(node["artifactId"], safe=".-_")
    version = urllib.parse.quote(node["version"], safe=".-_")
    purl = "pkg:maven/{}/{}@{}".format(group, artifact, version)
    qualifiers = []
    if node.get("classifier"):
        qualifiers.append("classifier=" + urllib.parse.quote(node["classifier"], safe=".-_"))
    if node.get("type") and node["type"] != "jar":
        qualifiers.append("type=" + urllib.parse.quote(node["type"], safe=".-_"))
    if qualifiers:
        purl += "?" + "&".join(sorted(qualifiers))
    return purl


def effective_scope(node):
    scope = node.get("scope", "") or "compile"
    if scope in ("test", "system"):
        return "excluded"
    if scope == "provided" or str(node.get("optional", "false")).lower() == "true":
        return "optional"
    return "required"


def convert(root, output):
    files = sorted(root.rglob("target/ddd4j-dependency-tree.json"))
    files = [path for path in files
             if "ddd4j-boot-samples" not in path.relative_to(root).parts]
    if not files:
        fail("No resolved Maven dependency trees were generated")
    components = {}
    edges = {}
    scopes = {}
    module_roots = set()
    precedence = {"excluded": 0, "optional": 1, "required": 2}

    def visit(node):
        ref = coordinate(node)
        scope = effective_scope(node)
        if ref not in components:
            components[ref] = {
                "type": "library",
                "bom-ref": ref,
                "group": node["groupId"],
                "name": node["artifactId"],
                "version": node["version"],
                "purl": ref,
            }
            edges[ref] = set()
            scopes[ref] = set()
        if precedence[scope] > precedence.get(components[ref].get("scope"), -1):
            components[ref]["scope"] = scope
        scopes[ref].add(node.get("scope", "") or "compile")
        for child in node.get("children", []) or []:
            child_ref = visit(child)
            edges[ref].add(child_ref)
        return ref

    for path in files:
        with path.open(encoding="utf-8") as source:
            tree = json.load(source)
        if not isinstance(tree, dict):
            fail("Invalid Maven dependency tree: " + str(path))
        module_roots.add(visit(tree))

    if not components or not any(not ref.startswith("pkg:maven/io.ddd4j.boot/")
                                 for ref in components):
        fail("SBOM has no resolved external dependencies")
    for ref, component in components.items():
        component["properties"] = [{"name": "maven:scope", "value": scope}
                                    for scope in sorted(scopes[ref])]
    root_ref = next((ref for ref in module_roots
                     if components[ref]["name"] == "ddd4j-boot"), None)
    if root_ref is None:
        root_ref = sorted(module_roots)[0]
    bom = {
        "bomFormat": "CycloneDX",
        "specVersion": "1.5",
        "serialNumber": "urn:uuid:" + str(uuid.uuid4()),
        "version": 1,
        "metadata": {
            "timestamp": datetime.datetime.now(datetime.timezone.utc).isoformat(),
            "component": components[root_ref],
        },
        "components": [components[ref] for ref in sorted(components)],
        "dependencies": [{"ref": ref, "dependsOn": sorted(edges[ref])}
                         for ref in sorted(edges)],
    }
    output.parent.mkdir(parents=True, exist_ok=True)
    with output.open("w", encoding="utf-8") as target:
        json.dump(bom, target, indent=2, ensure_ascii=False)
        target.write("\n")
    validate(output)
    print("[PASS] SBOM has {} components and {} dependency nodes from {} reactor trees".format(
        len(components), len(edges), len(files)))


def validate(path):
    with path.open(encoding="utf-8") as source:
        bom = json.load(source)
    if bom.get("bomFormat") != "CycloneDX" or not bom.get("specVersion"):
        fail("SBOM is not CycloneDX: " + str(path))
    components = bom.get("components") or []
    dependencies = bom.get("dependencies") or []
    if not components or not dependencies:
        fail("SBOM is empty or has no dependency graph: " + str(path))
    refs = {component.get("bom-ref") for component in components}
    if None in refs or len(refs) != len(components):
        fail("SBOM component references are missing or duplicated")
    for dependency in dependencies:
        if dependency.get("ref") not in refs:
            fail("SBOM dependency node has no component: " + repr(dependency))
        if any(child not in refs for child in dependency.get("dependsOn", [])):
            fail("SBOM dependency edge has no component: " + repr(dependency))
    print("[PASS] Validated {} SBOM components and {} dependency nodes".format(
        len(components), len(dependencies)))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path)
    parser.add_argument("--output", type=Path)
    parser.add_argument("--modules", action="store_true")
    parser.add_argument("--validate", type=Path)
    args = parser.parse_args()
    if args.validate:
        validate(args.validate)
        return
    if not args.root:
        parser.error("--root is required")
    root = args.root.resolve()
    if args.modules:
        print(",".join(reactor_modules(root)))
        return
    if not args.output:
        parser.error("--output is required")
    convert(root, args.output)


if __name__ == "__main__":
    main()
