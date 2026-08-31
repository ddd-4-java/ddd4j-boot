#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
auditor="${script_dir}/audit_auto_configuration_contracts.py"
tmp_dir="$(mktemp -d)"
trap 'rm -rf "${tmp_dir}"' EXIT

tree_dir="${tmp_dir}/tree"
mkdir -p "${tree_dir}/imports/META-INF/spring" "${tree_dir}/legacy/META-INF" "${tree_dir}/duplicate/META-INF/spring" "${tree_dir}/duplicate/META-INF"
printf 'org.example.CurrentAutoConfiguration\n' > "${tree_dir}/imports/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"
printf 'org.springframework.boot.autoconfigure.EnableAutoConfiguration=org.example.LegacyAutoConfiguration\n' > "${tree_dir}/legacy/META-INF/spring.factories"
printf 'org.example.DuplicateAutoConfiguration\n' > "${tree_dir}/duplicate/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"
printf 'org.springframework.boot.autoconfigure.EnableAutoConfiguration=org.example.DuplicateAutoConfiguration\n' > "${tree_dir}/duplicate/META-INF/spring.factories"
printf 'org.example.CurrentAutoConfiguration\norg.example.LegacyAutoConfiguration\norg.example.DuplicateAutoConfiguration\norg.example.MissingAutoConfiguration\n' > "${tmp_dir}/expected.txt"

python3 "${auditor}" --inspect-tree "${tree_dir}" --expected "${tmp_dir}/expected.txt" --output "${tmp_dir}/inventory.json"
rg -Fq '"status": "AUTO_CONFIGURATION"' "${tmp_dir}/inventory.json"
rg -Fq '"status": "LEGACY_FACTORY"' "${tmp_dir}/inventory.json"
rg -Fq '"status": "DUPLICATE_REGISTRATION"' "${tmp_dir}/inventory.json"
rg -Fq '"status": "MISSING"' "${tmp_dir}/inventory.json"

echo "PASS: auto-configuration registration formats"
