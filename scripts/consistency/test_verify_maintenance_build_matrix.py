"""Regression tests for feature and test branch build-contract selection."""

import importlib.util
from pathlib import Path
import unittest
from unittest.mock import patch


SOURCE = Path(__file__).with_name("verify_maintenance_build_matrix.py")
SPEC = importlib.util.spec_from_file_location("verify_maintenance_build_matrix", SOURCE)
MATRIX = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(MATRIX)


class MatrixBranchSelectionTest(unittest.TestCase):
    def setUp(self):
        self.rows = [
            {"branch": "3.4.x", "revision": "3.4.x.20260630-SNAPSHOT"},
            {"branch": "4.0.x", "revision": "4.0.x.20260630-SNAPSHOT"},
        ]

    def test_maintenance_branch_keeps_named_contract(self):
        self.assertEqual([self.rows[0]], MATRIX.select_rows(
            Path("."), self.rows, "3.4.x", "HEAD"))

    @patch.object(MATRIX, "git")
    def test_feature_and_test_branches_use_their_actual_pom_revision(self, git):
        git.return_value = (
            "<project><properties><revision>3.4.x.20260630-SNAPSHOT"
            "</revision></properties></project>"
        )
        for branch in ("feature/REQ-20261015-wandl-dependency-ci", "test/20261015"):
            with self.subTest(branch=branch):
                self.assertEqual([self.rows[0]], MATRIX.select_rows(
                    Path("."), self.rows, branch, "HEAD"))

    @patch.object(MATRIX, "git")
    def test_unknown_revision_cannot_bypass_matrix(self, git):
        git.return_value = "<project><properties><revision>unknown</revision></properties></project>"
        with self.assertRaisesRegex(ValueError, "no unique maintenance contract"):
            MATRIX.select_rows(Path("."), self.rows, "feature/REQ-20261015-wandl-dependency-ci", "HEAD")

    def test_other_unlisted_branch_remains_invalid(self):
        with self.assertRaisesRegex(ValueError, "not in build matrix"):
            MATRIX.select_rows(Path("."), self.rows, "backup/custom", "HEAD")


if __name__ == "__main__":
    unittest.main()
