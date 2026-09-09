"""Maven Model 4.1 父引用结构契约测试。"""

import tempfile
import unittest
from pathlib import Path

from verify_model41_parent_contract import verify


PROJECT = """\
<project xmlns="http://maven.apache.org/POM/4.1.0" {root_attribute}>
  <modelVersion>4.1.0</modelVersion>
  {parent}
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>{artifact_id}</artifactId>
  <version>1</version>
</project>
"""


class Model41ParentContractTest(unittest.TestCase):

    def write_project(self, root, relative, parent, root_project=False, artifact_id="child"):
        path = root / relative
        path.parent.mkdir(parents=True, exist_ok=True)
        root_attribute = 'root="true"' if root_project else ""
        path.write_text(PROJECT.format(
            parent=parent, root_attribute=root_attribute, artifact_id=artifact_id), encoding="utf-8")
        return path

    def test_reactor_parent_rejects_version_and_relative_path(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "pom.xml", "", root_project=True)
            self.write_project(root, "child/pom.xml", """
<parent>
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>parent</artifactId>
  <version>1</version>
  <relativePath>../pom.xml</relativePath>
</parent>""")

            errors = verify(root)

            self.assertTrue(any("parent must not declare relativePath" in error for error in errors))

    def test_external_parent_rejects_relative_path_element(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "pom.xml", """
<parent>
  <groupId>io.ddd4j</groupId>
  <artifactId>ddd4j-parent</artifactId>
  <version>3</version>
  <relativePath/>
</parent>""")

            errors = verify(root)

            self.assertTrue(any("parent must not declare relativePath" in error for error in errors))

    def test_reactor_parent_accepts_full_coordinates_without_relative_path(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "pom.xml", "", root_project=True)
            self.write_project(root, "child/pom.xml", """
<parent>
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>child</artifactId>
  <version>1</version>
</parent>""")

            self.assertEqual(verify(root), [])

    def test_reactor_parent_rejects_missing_version(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "pom.xml", "", root_project=True)
            self.write_project(root, "child/pom.xml", """
<parent>
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>child</artifactId>
</parent>""")

            errors = verify(root)

            self.assertTrue(any("parent must use full coordinates" in error for error in errors))

    def test_nested_reactor_parent_accepts_full_coordinates(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "pom.xml", "", root_project=True, artifact_id="root")
            self.write_project(root, "middle/pom.xml", """
<parent>
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>root</artifactId>
  <version>1</version>
</parent>""", artifact_id="middle")
            self.write_project(root, "middle/child/pom.xml", """
<parent>
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>middle</artifactId>
  <version>1</version>
</parent>""")

            self.assertEqual(verify(root), [])

    def test_external_parent_accepts_full_coordinates(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "pom.xml", """
<parent>
  <groupId>io.ddd4j</groupId>
  <artifactId>ddd4j-parent</artifactId>
  <version>3</version>
</parent>""")

            self.assertEqual(verify(root), [])

    def test_non_default_parent_path_requires_explicit_allowlist(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "pom.xml", "", root_project=True, artifact_id="root")
            self.write_project(root, "parent/pom.xml", """
<parent>
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>root</artifactId>
  <version>1</version>
</parent>""", artifact_id="parent")
            self.write_project(root, "child/pom.xml", """
<parent>
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>parent</artifactId>
  <version>1</version>
</parent>""")

            errors = verify(root)

            self.assertTrue(any("default parent path mismatch is not allowlisted" in error
                                for error in errors))

    def test_non_default_parent_path_accepts_exact_allowlist(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "pom.xml", "", root_project=True, artifact_id="root")
            self.write_project(root, "parent/pom.xml", """
<parent>
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>root</artifactId>
  <version>1</version>
</parent>""", artifact_id="parent")
            self.write_project(root, "child/pom.xml", """
<parent>
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>parent</artifactId>
  <version>1</version>
</parent>""")

            self.assertEqual(verify(root, {"child/pom.xml"}), [])

    def test_allowlist_rejects_stale_entry(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "pom.xml", "", root_project=True, artifact_id="root")

            errors = verify(root, {"removed/pom.xml"})

            self.assertTrue(any("allowlist entry no longer matches" in error for error in errors))


if __name__ == "__main__":
    unittest.main()
