"""Maven POM 内嵌 Shell 命令的回归测试。"""

import os
import re
import subprocess
import tempfile
import unittest
import xml.etree.ElementTree as ET
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[1]
PARENT_POM = PROJECT_ROOT / "ddd4j-boot-parent" / "pom.xml"
MAVEN_EXPRESSION = re.compile(r"\$\{[^}]+}")


def execution_script(execution_id):
    root = ET.parse(PARENT_POM).getroot()
    namespace = {"m": root.tag.split("}")[0][1:]}
    for execution in root.findall(".//m:execution", namespace):
        if execution.findtext("m:id", namespaces=namespace) != execution_id:
            continue
        arguments = execution.findall("m:configuration/m:arguments/m:argument", namespace)
        if len(arguments) >= 2 and arguments[0].text == "-c":
            return arguments[1].text or ""
    raise AssertionError(f"未找到 Shell 执行配置: {execution_id}")


def shell_scripts():
    root = ET.parse(PARENT_POM).getroot()
    namespace = {"m": root.tag.split("}")[0][1:]}
    for execution in root.findall(".//m:execution", namespace):
        arguments = execution.findall("m:configuration/m:arguments/m:argument", namespace)
        if len(arguments) >= 2 and arguments[0].text == "-c":
            yield execution.findtext("m:id", default="<unknown>", namespaces=namespace), arguments[1].text or ""


class EmbeddedShellCommandsTest(unittest.TestCase):

    def test_no_shell_option_is_split_into_an_independent_command(self):
        detached = []
        for execution_id, script in shell_scripts():
            for line_number, line in enumerate(script.splitlines(), start=1):
                if line.strip().startswith("--"):
                    detached.append(f"{execution_id}:{line_number}:{line.strip()}")
        self.assertEqual(detached, [], "XML 格式化拆断了 Shell 命令: " + ", ".join(detached))

    def test_buildx_verify_options_stay_with_their_commands(self):
        script = execution_script("docker-buildx-verify")
        stripped_lines = [line.strip() for line in script.splitlines()]

        self.assertFalse(
            any(line.startswith("--") for line in stripped_lines),
            "XML 格式化把 Shell 选项拆成了独立命令",
        )
        self.assertIn(
            'docker ps -a --filter "name=buildx_buildkit_${BUILDER_NAME}" --format "{{.Names}}"',
            script,
        )
        self.assertEqual(
            script.count(
                'docker buildx create --use --name "$BUILDER_NAME" '
                '--driver docker-container --driver-opt image="$BUILDKIT_IMAGE"'
            ),
            2,
        )

    def test_buildx_verify_is_valid_posix_shell(self):
        script = MAVEN_EXPRESSION.sub("value", execution_script("docker-buildx-verify"))
        result = subprocess.run(
            ["sh", "-n", "-c", script],
            check=False,
            capture_output=True,
            text=True,
        )
        self.assertEqual(result.returncode, 0, result.stderr)

    def test_buildx_verify_passes_options_to_docker(self):
        script = MAVEN_EXPRESSION.sub("value", execution_script("docker-buildx-verify"))
        script = script.replace("sleep 2", ":")
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            docker_log = directory / "docker.log"
            docker = directory / "docker"
            docker.write_text(
                "#!/bin/sh\n"
                'printf "%s\\n" "$*" >> "$DOCKER_LOG"\n'
                'if [ "$1 $2" = "ps -a" ]; then\n'
                '  printf "%s\\n" "buildx_buildkit_value0"\n'
                "fi\n"
            )
            docker.chmod(0o755)
            environment = os.environ.copy()
            environment["PATH"] = f"{directory}:{environment['PATH']}"
            environment["DOCKER_LOG"] = str(docker_log)

            result = subprocess.run(
                ["sh", "-c", script],
                check=False,
                capture_output=True,
                text=True,
                env=environment,
            )

            self.assertEqual(result.returncode, 0, result.stderr)
            self.assertNotIn("command not found", result.stderr)
            calls = docker_log.read_text()
            self.assertIn(
                "ps -a --filter name=buildx_buildkit_value --format {{.Names}}",
                calls,
            )
            self.assertIn(
                "buildx create --use --name value --driver docker-container "
                "--driver-opt image=value",
                calls,
            )


if __name__ == "__main__":
    unittest.main()
