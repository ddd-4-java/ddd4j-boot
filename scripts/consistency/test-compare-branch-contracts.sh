#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
comparator="${script_dir}/compare_branch_contracts.py"
tmp_dir="$(mktemp -d)"
trap 'rm -rf "${tmp_dir}"' EXIT
header='from_group\tto_group\tcontract_id\tsource_surface\ttarget_surface\tbehavior_delta\tvalidation_command\n'
printf "${header}jdk8\tjdk17\tWEBMVC_WEBFLUX\tjavax.servlet\tjakarta.servlet\tsame HTTP error semantics\tmvn -pl ddd4j-boot-core -am test\n" > "${tmp_dir}/valid-map.tsv"
printf "${header}jdk8\tjdk17\tWEBMVC_WEBFLUX\t\tjakarta.servlet\t\tmvn -pl ddd4j-boot-core -am test\n" > "${tmp_dir}/invalid-map.tsv"
printf 'jdk_group\tsurface\tvalue\tdeprecated\njdk17\tddd4j.web.public-paths\t/api/**\tfalse\n' > "${tmp_dir}/baseline.tsv"
printf 'jdk_group\tsurface\tvalue\tdeprecated\njdk17\tddd4j.web.public-paths\t/public/**\tfalse\n' > "${tmp_dir}/drift.tsv"
printf 'jdk_group\tsurface\tvalue\tdeprecated\n' > "${tmp_dir}/removed.tsv"

if python3 "${comparator}" --migration-map "${tmp_dir}/invalid-map.tsv" --baseline-surfaces "${tmp_dir}/baseline.tsv" --candidate-surfaces "${tmp_dir}/baseline.tsv" --output "${tmp_dir}/invalid.json" >"${tmp_dir}/invalid.out" 2>&1; then
  echo 'expected incomplete cross-group mapping to fail' >&2
  exit 1
fi
rg -Fq 'requires source_surface' "${tmp_dir}/invalid.out"

if python3 "${comparator}" --migration-map "${tmp_dir}/valid-map.tsv" --baseline-surfaces "${tmp_dir}/baseline.tsv" --candidate-surfaces "${tmp_dir}/drift.tsv" --output "${tmp_dir}/drift.json" >"${tmp_dir}/drift.out" 2>&1; then
  echo 'expected undocumented same-group drift to fail' >&2
  exit 1
fi
rg -Fq 'undocumented same-group drift' "${tmp_dir}/drift.out"

if python3 "${comparator}" --migration-map "${tmp_dir}/valid-map.tsv" --baseline-surfaces "${tmp_dir}/baseline.tsv" --candidate-surfaces "${tmp_dir}/removed.tsv" --output "${tmp_dir}/removed.json" >"${tmp_dir}/removed.out" 2>&1; then
  echo 'expected undocumented same-group removal to fail' >&2
  exit 1
fi
rg -Fq 'undocumented same-group removal' "${tmp_dir}/removed.out"

python3 "${comparator}" --migration-map "${tmp_dir}/valid-map.tsv" --baseline-surfaces "${tmp_dir}/baseline.tsv" --candidate-surfaces "${tmp_dir}/baseline.tsv" --output "${tmp_dir}/valid.json"
rg -Fq '"status": "ADAPTED"' "${tmp_dir}/valid.json"

echo 'PASS: branch contract comparison'
