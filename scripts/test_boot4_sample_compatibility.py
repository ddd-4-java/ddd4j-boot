"""Boot 4 样例源码兼容契约回归测试。"""

import tempfile
import unittest
from pathlib import Path

from verify_boot4_sample_compatibility import verify


class Boot4SampleCompatibilityTest(unittest.TestCase):

    def write(self, root, relative_path, content):
        path = root / relative_path
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content, encoding="utf-8")
        return path

    def test_rejects_each_known_boot4_incompatibility(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write(
                root,
                "ddd4j-boot-samples/demo/src/main/java/example/DemoApplication.java",
                """\
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import com.github.dozermapper.extra.converters.BooleanStringConverter;
import com.baomidou.mybatisplus.extension.service.IService;
""",
            )
            self.write(
                root,
                "ddd4j-boot-samples/demo/pom.xml",
                """\
<project><dependencies><dependency>
<groupId>io.github.easy4j</groupId>
<artifactId>dozer-extra-converters</artifactId>
</dependency></dependencies></project>
""",
            )

            errors = verify(root)

            self.assertTrue(any("old MeterRegistryCustomizer package" in item for item in errors))
            self.assertTrue(any("empty Dozer converter API" in item for item in errors))
            self.assertTrue(any("old MyBatis-Plus service package" in item for item in errors))
            self.assertTrue(any("empty dozer-extra-converters dependency" in item for item in errors))

    def test_accepts_boot4_packages_without_empty_converter(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write(
                root,
                "ddd4j-boot-samples/demo/src/main/java/example/DemoApplication.java",
                """\
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import com.baomidou.mybatisplus.spring.service.IService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
""",
            )
            self.write(
                root,
                "ddd4j-boot-samples/demo/pom.xml",
                "<project><dependencies /></project>",
            )

            self.assertEqual(verify(root), [])


if __name__ == "__main__":
    unittest.main()
