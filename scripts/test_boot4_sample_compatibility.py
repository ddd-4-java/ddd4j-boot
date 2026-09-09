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
                "ddd4j-boot-samples/demo/src/test/java/example/DemoApplicationTest.java",
                """\
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.test.web.client.TestRestTemplate;
import com.github.dozermapper.extra.converters.BooleanStringConverter;
import com.github.hiwepy.validation.constraints.FileNotEmpty;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import org.springframework.web.reactive.resource.WebJarsResourceResolver;
class LegacyMediaType { String value = org.springframework.http.MediaType.APPLICATION_STREAM_JSON_VALUE; }
""",
            )
            self.write(
                root,
                "pom.xml",
                "<project><dependencies /></project>",
            )
            self.write(
                root,
                "ddd4j-boot-samples/demo/pom.xml",
                """\
<project><dependencies><dependency>
<groupId>io.github.easy4j</groupId>
<artifactId>dozer-extra-converters</artifactId>
</dependency><dependency>
<groupId>io.github.resilience4j</groupId>
<artifactId>resilience4j-spring-boot2</artifactId>
</dependency></dependencies></project>
""",
            )

            errors = verify(root)

            self.assertTrue(any("old MeterRegistryCustomizer package" in item for item in errors))
            self.assertTrue(any("old TestRestTemplate package" in item for item in errors))
            self.assertTrue(any("missing AutoConfigureTestRestTemplate" in item for item in errors))
            self.assertTrue(any("missing spring-boot-resttestclient" in item for item in errors))
            self.assertTrue(any("Boot 2 Resilience4j starter" in item for item in errors))
            self.assertTrue(any("empty Dozer converter API" in item for item in errors))
            self.assertTrue(any("old Easy4J validation package" in item for item in errors))
            self.assertTrue(any("old MyBatis-Plus service package" in item for item in errors))
            self.assertTrue(any("old MyBatis-Plus active record package" in item for item in errors))
            self.assertTrue(any("old WebJars resource resolver" in item for item in errors))
            self.assertTrue(any("removed stream JSON media type" in item for item in errors))
            self.assertTrue(any("empty dozer-extra-converters dependency" in item for item in errors))

    def test_accepts_boot4_packages_without_empty_converter(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write(
                root,
                "ddd4j-boot-samples/demo/src/main/java/example/DemoApplication.java",
                """\
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import com.baomidou.mybatisplus.spring.service.IService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;

@AutoConfigureTestRestTemplate
class DemoApplicationTest {}
""",
            )
            self.write(
                root,
                "pom.xml",
                """\
<project><dependencies><dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-resttestclient</artifactId>
<scope>test</scope>
</dependency></dependencies></project>
""",
            )
            self.write(
                root,
                "ddd4j-boot-samples/demo/pom.xml",
                "<project><dependencies /></project>",
            )

            self.assertEqual(verify(root), [])

    def test_rejects_boot4_bom_without_resilience4j_boot4_management(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            self.write(
                root,
                "ddd4j-boot-dependencies/pom.xml",
                """\
<project><properties>
<resilience4j-spring-boot2.version>2.3.0</resilience4j-spring-boot2.version>
</properties><dependencyManagement><dependencies><dependency>
<groupId>io.github.resilience4j</groupId>
<artifactId>resilience4j-spring-boot2</artifactId>
<version>${resilience4j-spring-boot2.version}</version>
</dependency></dependencies></dependencyManagement></project>
""",
            )
            self.write(
                root,
                "pom.xml",
                """\
<project><dependencies><dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-resttestclient</artifactId>
</dependency></dependencies></project>
""",
            )

            errors = verify(root)

            self.assertTrue(any("must alias resilience4j-spring-boot4 to resilience4j.version" in item
                                for item in errors))


if __name__ == "__main__":
    unittest.main()
