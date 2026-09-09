#!/usr/bin/env python3
"""验证 Boot 4 样例不再引用已迁移或无实现的依赖 API。"""

import argparse
import sys
from pathlib import Path


FORBIDDEN = {
    "org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer":
        "old MeterRegistryCustomizer package",
    "com.github.dozermapper.extra.converters": "empty Dozer converter API",
    "com.baomidou.mybatisplus.extension.service": "old MyBatis-Plus service package",
}


def verify(root):
    root = Path(root)
    errors = []
    for source in sorted(root.glob("ddd4j-boot-samples/**/src/main/java/**/*.java")):
        content = source.read_text(encoding="utf-8")
        for token, reason in FORBIDDEN.items():
            if token in content:
                errors.append(f"{source}: {reason}: {token}")
    for pom in sorted(root.glob("ddd4j-boot-samples/**/pom.xml")):
        content = pom.read_text(encoding="utf-8")
        if "<artifactId>dozer-extra-converters</artifactId>" in content:
            errors.append(f"{pom}: empty dozer-extra-converters dependency")
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
