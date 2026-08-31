#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd "${script_dir}/../.." && pwd)"
auditor="${script_dir}/audit_branch_baselines.py"
fixtures="${script_dir}/fixtures"
tmp_dir="$(mktemp -d)"
trap 'rm -rf "${tmp_dir}"' EXIT

tree_dir="${tmp_dir}/tree"
mkdir -p "${tree_dir}/starter/src/main/resources/META-INF/spring"
printf 'org.example.LegacyAutoConfiguration\n' > "${tree_dir}/starter/src/main/resources/META-INF/spring.factories"
printf 'org.example.CurrentAutoConfiguration\n' > "${tree_dir}/starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"

assert_inspection() {
  local fixture="$1"
  local expected_java="$2"
  local expected_boot="$3"
  local output="${tmp_dir}/${fixture}.json"
  python3 "${auditor}" --inspect-pom "${fixtures}/${fixture}.xml" --tree "${tree_dir}" --output "${output}"
  rg -Fq "\"java_version\": \"${expected_java}\"" "${output}"
  rg -Fq "\"spring_boot_version\": \"${expected_boot}\"" "${output}"
  rg -Fq '"spring_factories_entry_count": 1' "${output}"
  rg -Fq '"auto_configuration_import_entry_count": 1' "${output}"
  rg -Fq '"ddd4j-boot-core"' "${output}"
}

assert_inspection baseline-jdk8-pom 1.8 2.6.15
assert_inspection baseline-jdk17-pom 17 3.4.13
assert_inspection baseline-jdk21-pom 21 4.1.0

printf '<project><broken></project>' > "${tmp_dir}/invalid-pom.xml"
if python3 "${auditor}" --inspect-pom "${tmp_dir}/invalid-pom.xml" --tree "${tree_dir}" --output "${tmp_dir}/invalid.json" >"${tmp_dir}/invalid.out" 2>&1; then
  echo "expected invalid POM to fail" >&2
  exit 1
fi
rg -Fq 'invalid XML' "${tmp_dir}/invalid.out"

echo "PASS: branch baseline fixture audit"
