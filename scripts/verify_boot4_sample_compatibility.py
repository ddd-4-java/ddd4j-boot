#!/usr/bin/env python3
"""验证 Boot 4 样例不再引用已迁移或无实现的依赖 API。"""

import argparse
import sys
from pathlib import Path


FORBIDDEN = {
    "org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer":
        "old MeterRegistryCustomizer package",
    "org.springframework.boot.test.web.client.TestRestTemplate": "old TestRestTemplate package",
    "com.github.dozermapper.extra.converters": "empty Dozer converter API",
    "com.github.hiwepy.validation.constraints": "old Easy4J validation package",
    "com.baomidou.mybatisplus.extension.service": "old MyBatis-Plus service package",
    "com.baomidou.mybatisplus.extension.activerecord": "old MyBatis-Plus active record package",
    "org.springframework.web.reactive.resource.WebJarsResourceResolver":
        "old WebJars resource resolver",
    "MediaType.APPLICATION_STREAM_JSON_VALUE": "removed stream JSON media type",
}


def verify(root):
    root = Path(root)
    errors = []
    for source in sorted(root.glob("ddd4j-boot-samples/**/src/**/*.java")):
        content = source.read_text(encoding="utf-8")
        for token, reason in FORBIDDEN.items():
            if token in content:
                errors.append(f"{source}: {reason}: {token}")
        if ("TestRestTemplate" in content and
                "AutoConfigureTestRestTemplate" not in content):
            errors.append(f"{source}: missing AutoConfigureTestRestTemplate")
    for pom in sorted(root.glob("ddd4j-boot-samples/**/pom.xml")):
        content = pom.read_text(encoding="utf-8")
        if "<artifactId>dozer-extra-converters</artifactId>" in content:
            errors.append(f"{pom}: empty dozer-extra-converters dependency")
        if "<artifactId>resilience4j-spring-boot2</artifactId>" in content:
            errors.append(f"{pom}: Boot 2 Resilience4j starter in Boot 4 sample")
    root_pom = root / "pom.xml"
    root_content = root_pom.read_text(encoding="utf-8") if root_pom.exists() else ""
    if "<artifactId>spring-boot-resttestclient</artifactId>" not in root_content:
        errors.append(f"{root_pom}: missing spring-boot-resttestclient test dependency")
    boot_bom = root / "ddd4j-boot-dependencies/pom.xml"
    if boot_bom.exists():
        boot_bom_content = boot_bom.read_text(encoding="utf-8")
        expected_property = "<resilience4j-spring-boot4.version>2.4.0</resilience4j-spring-boot4.version>"
        expected_artifact = "<artifactId>resilience4j-spring-boot4</artifactId>"
        if expected_property not in boot_bom_content or expected_artifact not in boot_bom_content:
            errors.append(f"{boot_bom}: must manage resilience4j-spring-boot4 at 2.4.0")
    return errors


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("root", nargs="?", default=".", type=Path)
    args = parser.parse_args()
    errors = verify(args.root)
    if errors:
        print("\n".join(errors))
        return 1
    print("PASS: Boot 4 sample compatibility contract")
    return 0


if __name__ == "__main__":
    sys.exit(main())
