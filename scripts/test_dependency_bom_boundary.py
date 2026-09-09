"""Boot BOM 与 ddd4j 上游依赖边界回归。"""

import tempfile
import unittest
from pathlib import Path

from verify_dependency_bom_boundary import (
    BASE_COMPONENTS,
    OwnershipRule,
    load_ownership,
    verify,
    verify_ownership,
)


def pom(properties, dependencies):
    props = "\n".join(f"<{name}>1</{name}>" for name in properties)
    deps = "\n".join(
        f"<dependency><groupId>{group}</groupId><artifactId>{artifact}</artifactId>"
        + (f"<version>{version}</version>" if version else "") + "</dependency>"
        for group, artifact, version in dependencies)
    return (f'<project xmlns="http://maven.apache.org/POM/4.0.0"><properties>{props}</properties>'
            f'<dependencyManagement><dependencies>{deps}</dependencies></dependencyManagement></project>')


class DependencyBomBoundaryTest(unittest.TestCase):
    def write(self, directory, name, content):
        path = directory / name
        path.write_text(content)
        return path

    def test_base_components_use_current_truelicense_coordinates(self):
        self.assertIn(("global.namespace.truelicense", "truelicense-v1"), BASE_COMPONENTS)
        self.assertNotIn(("de.schlichtherle.truelicense", "truelicense-core"), BASE_COMPONENTS)
        self.assertNotIn(("de.schlichtherle.truelicense", "truelicense-xml"), BASE_COMPONENTS)

    def test_base_components_must_be_managed_by_every_upstream_line(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            boot = self.write(directory, "boot.xml", pom(["spring-boot-starter-demo.version"], []))
            complete = [(group, artifact, "1") for group, artifact in BASE_COMPONENTS]
            jdk8 = self.write(directory, "8.xml", pom([], complete[:-1]))
            jdk17 = self.write(directory, "17.xml", pom([], complete))
            errors = verify(boot, [("8", jdk8), ("17", jdk17)])
            self.assertTrue(any("ddd4j 8 does not effectively manage" in error for error in errors))

    def test_boot_rejects_non_boot_property_and_direct_version(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            complete = [(group, artifact, "1") for group, artifact in BASE_COMPONENTS]
            boot = self.write(directory, "boot.xml", pom(["zxing-extension.version"], complete[:1]))
            upstream = self.write(directory, "upstream.xml", pom([], complete))
            errors = verify(boot, [("8", upstream)])
            self.assertTrue(any("non-Spring-Boot properties" in error for error in errors))
            self.assertTrue(any("directly versions base component" in error for error in errors))

    def test_clean_boundary_passes(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            complete = [(group, artifact, "1") for group, artifact in BASE_COMPONENTS]
            boot = self.write(directory, "boot.xml", pom(
                ["spring-boot-starter-demo.version", "resilience4j-spring-boot2.version"],
                [(group, artifact, None) for group, artifact in BASE_COMPONENTS]))
            upstream = self.write(directory, "upstream.xml", pom([], complete))
            self.assertEqual(verify(boot, [("8", upstream), ("17", upstream), ("21", upstream)]), [])

    def test_boot_rejects_platform_property_and_direct_version(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            boot = self.write(directory, "boot.xml", pom(
                ["slf4j.version"], [("org.slf4j", "slf4j-api", "${slf4j.version}")]))
            rules = [OwnershipRule("platform", "org.slf4j", "slf4j-api")]

            errors = verify_ownership(boot, rules)

            self.assertIn("Boot BOM directly versions platform component org.slf4j:slf4j-api", errors)
            self.assertIn("Boot BOM owns platform version property slf4j.version", errors)

    def test_boot_accepts_boot_starter(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            boot = self.write(directory, "boot.xml", pom(
                ["spring-boot-starter-web.version"],
                [("org.springframework.boot", "spring-boot-starter-web",
                  "${spring-boot-starter-web.version}")]))
            rules = [OwnershipRule(
                "spring-boot", "org.springframework.boot", "spring-boot-starter-web")]

            self.assertEqual(verify_ownership(boot, rules), [])

    def test_boot_rejects_orphaned_platform_version_property(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            boot = self.write(directory, "boot.xml", pom(["jackson-annotations.version"], []))
            rules = [OwnershipRule(
                "platform", "com.fasterxml.jackson.core", "jackson-annotations",
                "jackson-annotations.version")]

            errors = verify_ownership(boot, rules)

            self.assertIn("Boot BOM owns platform version property jackson-annotations.version", errors)

    def test_ownership_manifest_loads_coordinate_level_rules(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            manifest = self.write(directory, "ownership.tsv", """\
scope\tgroup_id\tartifact_id\tproperty_name
platform\torg.slf4j\tslf4j-api\tslf4j.version
spring-boot\torg.springframework.boot\tspring-boot-starter-web\tspring-boot-starter-web.version
""")

            self.assertEqual(load_ownership(manifest), [
                OwnershipRule("platform", "org.slf4j", "slf4j-api", "slf4j.version"),
                OwnershipRule("spring-boot", "org.springframework.boot", "spring-boot-starter-web",
                              "spring-boot-starter-web.version"),
            ])


if __name__ == "__main__":
    unittest.main()
