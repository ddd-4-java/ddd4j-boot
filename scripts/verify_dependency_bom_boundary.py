#!/usr/bin/env python3
"""验证 Boot BOM 只声明 Spring Boot 版本，基础组件由 ddd4j 三线管理。"""

import argparse
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

BASE_COMPONENTS = {
    ("global.namespace.truelicense", "truelicense-v1"),
    ("io.github.easy4j", "zxing-extension"),
    ("com.baomidou", "mybatis-plus-spring"),
}


def model(path):
    root = ET.parse(path).getroot()
    namespace = {"m": root.tag.split("}")[0][1:]}
    properties = root.find("m:properties", namespace)
    names = [element.tag.split("}")[-1] for element in properties] if properties is not None else []
    dependencies = {}
    for dependency in root.findall("m:dependencyManagement/m:dependencies/m:dependency", namespace):
        group = dependency.findtext("m:groupId", default="", namespaces=namespace)
        artifact = dependency.findtext("m:artifactId", default="", namespaces=namespace)
        dependencies[(group, artifact)] = dependency.findtext("m:version", namespaces=namespace)
    return names, dependencies


def verify(boot_pom, upstream_effective):
    errors = []
    properties, boot_dependencies = model(boot_pom)
    invalid = sorted(name for name in properties
                     if not name.startswith("spring-boot-")
                     and name != "resilience4j-spring-boot2.version")
    if invalid:
        errors.append("Boot BOM contains non-Spring-Boot properties: " + ", ".join(invalid))
    for coordinate in BASE_COMPONENTS:
        version = boot_dependencies.get(coordinate)
        if version is not None:
            errors.append("Boot BOM directly versions base component " + ":".join(coordinate))
    for label, path in upstream_effective:
        _, dependencies = model(path)
        for coordinate in sorted(BASE_COMPONENTS):
            version = dependencies.get(coordinate)
            if not version or "${" in version:
                errors.append(f"ddd4j {label} does not effectively manage {':'.join(coordinate)}")
    return errors


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--boot-pom", type=Path, required=True)
    parser.add_argument("--upstream-effective", action="append", nargs=2,
                        metavar=("JDK", "POM"), required=True)
    args = parser.parse_args()
    effective = [(label, Path(path)) for label, path in args.upstream_effective]
    errors = verify(args.boot_pom, effective)
    if errors:
        print("\n".join(errors))
        return 1
    print(f"PASS: Boot BOM boundary and {len(effective)} ddd4j dependency lines")
    return 0


if __name__ == "__main__":
    sys.exit(main())
