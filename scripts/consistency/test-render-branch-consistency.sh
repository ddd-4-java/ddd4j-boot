#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
renderer="${script_dir}/render_branch_consistency.py"
tmp_dir="$(mktemp -d)"
trap 'rm -rf "${tmp_dir}"' EXIT

printf 'branch\tjdk_group\tboot_line\tddd4j_line\tregistration_mode\n3.4.x\tjdk17\t3.4.x\t2.0.x\timports\n' > "${tmp_dir}/branches.tsv"
printf 'contract_id\tcapability\tobservable_behavior\trequired_state\towner\nAUTO_CONFIGURATION\tauto-configuration\tuser bean override\tREQUIRED\tddd4j-boot-core\n' > "${tmp_dir}/contracts.tsv"
printf '{"branch_count":1,"branches":[{"branch":"3.4.x","commit":"abc","java_version":"17","spring_boot_version":"3.4.13","revision":"3.4.x"}]}' > "${tmp_dir}/baseline.json"
printf '{"branch_count":1,"audit_status":"PASS","branches":[{"branch":"3.4.x","unmapped_registration_count":0,"duplicate_registration_count":0}]}' > "${tmp_dir}/auto.json"

run_failure() {
  local name="$1"
  local exceptions="$2"
  set +e
  python3 "${renderer}" --branches "${tmp_dir}/branches.tsv" --contracts "${tmp_dir}/contracts.tsv" --exceptions "${exceptions}" --baseline "${tmp_dir}/baseline.json" --auto-config "${tmp_dir}/auto.json" --output "${tmp_dir}/${name}.md"
  local run_exit=$?
  set -e
  test "${run_exit}" -ne 0
}

printf 'branch\tcontract_id\tstatus\treason\tmigration_document\tevidence_ref\n' > "${tmp_dir}/missing-evidence.tsv"
run_failure missing-evidence "${tmp_dir}/missing-evidence.tsv"
rg -Fq 'MISSING_EVIDENCE' "${tmp_dir}/missing-evidence.md"

printf 'branch\tcontract_id\tstatus\treason\tmigration_document\tevidence_ref\n3.4.x\tAUTO_CONFIGURATION\tBLOCKED\t\t\tissue\n' > "${tmp_dir}/blocked-no-reason.tsv"
run_failure blocked-no-reason "${tmp_dir}/blocked-no-reason.tsv"

printf 'branch\tcontract_id\tstatus\treason\tmigration_document\tevidence_ref\n3.4.x\tAUTO_CONFIGURATION\tNOT_APPLICABLE\tremoved\t\tissue\n' > "${tmp_dir}/na-no-migration.tsv"
run_failure na-no-migration "${tmp_dir}/na-no-migration.tsv"

printf 'branch\tcontract_id\tstatus\treason\tmigration_document\tevidence_ref\n3.4.x\tAUTO_CONFIGURATION\tREQUIRED\t\t\ttest-command\n' > "${tmp_dir}/pass.tsv"
python3 "${renderer}" --branches "${tmp_dir}/branches.tsv" --contracts "${tmp_dir}/contracts.tsv" --exceptions "${tmp_dir}/pass.tsv" --baseline "${tmp_dir}/baseline.json" --auto-config "${tmp_dir}/auto.json" --output "${tmp_dir}/pass.md"
rg -Fq '| 3.4.x | AUTO_CONFIGURATION | PASS |' "${tmp_dir}/pass.md"

echo "PASS: consistency evidence rendering"
