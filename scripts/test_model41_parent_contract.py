"""Maven Model 4.1 父引用结构契约测试。"""

import tempfile
import unittest
from pathlib import Path

from verify_model41_parent_contract import verify


PROJECT = """\
<project xmlns="http://maven.apache.org/POM/4.1.0">
  <modelVersion>4.1.0</modelVersion>
  {parent}
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>child</artifactId>
  <version>1</version>
</project>
"""


class Model41ParentContractTest(unittest.TestCase):

    def write_project(self, root, relative, parent):
        path = root / relative
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(PROJECT.format(parent=parent), encoding="utf-8")
        return path

    def test_reactor_parent_rejects_coordinates_with_relative_path(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "pom.xml", "")
            self.write_project(root, "child/pom.xml", """
<parent>
  <groupId>io.ddd4j.boot</groupId>
  <artifactId>parent</artifactId>
  <version>1</version>
  <relativePath>../pom.xml</relativePath>
</parent>""")

            errors = verify(root)

            self.assertTrue(any("reactor parent must use relativePath only" in error for error in errors))

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

            self.assertTrue(any("external parent must use coordinates only" in error for error in errors))

    def test_reactor_parent_accepts_existing_relative_path_only(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "pom.xml", "")
            self.write_project(root, "child/pom.xml", """
<parent>
  <relativePath>../pom.xml</relativePath>
</parent>""")

            self.assertEqual(verify(root), [])

    def test_reactor_parent_rejects_missing_relative_target(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write_project(root, "child/pom.xml", """
<parent>
  <relativePath>../missing/pom.xml</relativePath>
</parent>""")

            errors = verify(root)

            self.assertTrue(any("relativePath does not exist" in error for error in errors))


if __name__ == "__main__":
    unittest.main()
