"""Regression tests for component-level release license checks."""

import importlib.util
import json
from pathlib import Path
import tempfile
import unittest


MODULE = Path(__file__).with_name("check-license-policy.py")
SPEC = importlib.util.spec_from_file_location("check_license_policy", MODULE)
POLICY = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(POLICY)


class LicensePolicyTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        root = Path(self.temp.name)
        self.report = root / "THIRD-PARTY.txt"
        self.sbom = root / "sbom.json"
        self.selections = root / "selections.tsv"
        self.exclusions = root / "exclusions.tsv"
        self.report.write_text(
            "    LGPL:\n"
            "        * Logback (ch.qos.logback:logback-core:1.5.36 - example)\n"
            "    EPL-2.0:\n"
            "        * Logback (ch.qos.logback:logback-core:1.5.36 - example)\n"
            "    GPL:\n"
            "        * Bad (example:bad:1 - example)\n",
            encoding="utf-8",
        )
        self._write_sbom(["ch.qos.logback:logback-core:1.5.36", "example:bad:1"])

    def _write_sbom(self, coordinates):
        components = []
        for coordinate in coordinates:
            group, name, version = coordinate.split(":")
            components.append({
                "bom-ref": f"pkg:maven/{group}/{name}@{version}",
                "group": group, "name": name, "version": version,
            })
        self.sbom.write_text(json.dumps({
            "bomFormat": "CycloneDX",
            "components": components,
            "dependencies": [{"ref": item["bom-ref"], "dependsOn": []}
                             for item in components],
        }), encoding="utf-8")

    def test_selected_compatible_license_does_not_hide_other_component(self):
        self.selections.write_text("ch.qos.logback:logback-core:1.5.36\tEPL-2.0\n")
        passes, errors = POLICY.check(
            self.report, self.sbom, self.selections, self.exclusions)
        self.assertEqual(1, len(passes))
        self.assertEqual(1, len(errors))
        self.assertIn("example:bad:1", errors[0])

    def test_exclusion_requires_component_absent_from_sbom(self):
        self.selections.write_text("ch.qos.logback:logback-core:1.5.36\tEPL-2.0\n")
        self.exclusions.write_text("example:bad:1\tbuild tool\n")
        self.assertEqual(1, len(POLICY.check(
            self.report, self.sbom, self.selections, self.exclusions)[1]))
        self._write_sbom(["ch.qos.logback:logback-core:1.5.36"])
        self.assertEqual([], POLICY.check(
            self.report, self.sbom, self.selections, self.exclusions)[1])

    def test_blocked_selection_and_empty_sbom_fail(self):
        self.selections.write_text("ch.qos.logback:logback-core:1.5.36\tLGPL\n")
        self.assertIn("not allowed", POLICY.check(
            self.report, self.sbom, self.selections, self.exclusions)[1][0])
        self.sbom.write_text('{"bomFormat":"CycloneDX","components":[],"dependencies":[]}')
        with self.assertRaisesRegex(ValueError, "empty"):
            POLICY.check(self.report, self.sbom, self.selections, self.exclusions)

    def test_exact_apache_2_license_aliases_are_valid_selections(self):
        coordinate = "example:dual-licensed:1"
        for name in ("AL 2.0", "Apache License 2.0", "Apache License v2.0"):
            with self.subTest(name=name):
                self.report.write_text(
                    f"    LGPL:\n        * Dual ({coordinate} - example)\n"
                    f"    {name}:\n        * Dual ({coordinate} - example)\n",
                    encoding="utf-8",
                )
                self._write_sbom([coordinate])
                self.selections.write_text(f"{coordinate}\t{name}\n", encoding="utf-8")
                self.assertEqual([], POLICY.check(
                    self.report, self.sbom, self.selections, self.exclusions)[1])

        self.report.write_text(
            f"    LGPL:\n        * Dual ({coordinate} - example)\n"
            f"    Apache License 2.1:\n        * Dual ({coordinate} - example)\n",
            encoding="utf-8",
        )
        self.selections.write_text(f"{coordinate}\tApache License 2.1\n", encoding="utf-8")
        self.assertEqual(1, len(POLICY.check(
            self.report, self.sbom, self.selections, self.exclusions)[1]))


if __name__ == "__main__":
    unittest.main()
