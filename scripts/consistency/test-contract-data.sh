#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd "${script_dir}/../.." && pwd)"
validator="${script_dir}/verify_contract_data.py"
tmp_dir="$(mktemp -d)"
trap 'rm -rf "${tmp_dir}"' EXIT

assert_fails() {
  local name="$1"
  shift
  if "$@" >"${tmp_dir}/${name}.out" 2>&1; then
    echo "expected ${name} to fail" >&2
    exit 1
  fi
}

assert_fails_contains() {
  local name="$1"
  local expected="$2"
  shift 2
  assert_fails "${name}" "$@"
  if ! rg -Fq "${expected}" "${tmp_dir}/${name}.out"; then
    echo "${name} did not report: ${expected}" >&2
    cat "${tmp_dir}/${name}.out" >&2
    exit 1
  fi
}

sed '/^4\.1\.x\t/d' \
  "${repo_root}/config/consistency/ddd4j-boot-branch-groups.tsv" \
  > "${tmp_dir}/missing-branch.tsv"
printf 'branch\tcontract_id\tstatus\treason\tmigration_document\tevidence_ref\n2.3.x\tCORE_SPI_REPOSITORY_CQRS\tUNKNOWN\treason\t\tfixture\n' \
  > "${tmp_dir}/unsupported-status.tsv"
printf 'branch\tcontract_id\tstatus\treason\tmigration_document\tevidence_ref\n2.3.x\tMQ\tNOT_APPLICABLE\tplatform removed\t\tfixture\n' \
  > "${tmp_dir}/missing-migration.tsv"
sed 's/\tddd4j-boot-core$/\t/' \
  "${repo_root}/config/consistency/ddd4j-boot-logical-contracts.tsv" \
  > "${tmp_dir}/missing-owner.tsv"

assert_fails_contains missing_branch "must contain exactly 13 maintenance branches" python3 "${validator}" \
  --branches "${tmp_dir}/missing-branch.tsv" \
  --contracts "${repo_root}/config/consistency/ddd4j-boot-logical-contracts.tsv" \
  --exceptions "${repo_root}/config/consistency/ddd4j-boot-contract-exceptions.tsv"

assert_fails_contains unsupported_status "unsupported status: UNKNOWN" python3 "${validator}" \
  --branches "${repo_root}/config/consistency/ddd4j-boot-branch-groups.tsv" \
  --contracts "${repo_root}/config/consistency/ddd4j-boot-logical-contracts.tsv" \
  --exceptions "${tmp_dir}/unsupported-status.tsv"

assert_fails_contains missing_migration "NOT_APPLICABLE requires migration document" python3 "${validator}" \
  --branches "${repo_root}/config/consistency/ddd4j-boot-branch-groups.tsv" \
  --contracts "${repo_root}/config/consistency/ddd4j-boot-logical-contracts.tsv" \
  --exceptions "${tmp_dir}/missing-migration.tsv"

assert_fails_contains missing_owner "contract table has blank owner" python3 "${validator}" \
  --branches "${repo_root}/config/consistency/ddd4j-boot-branch-groups.tsv" \
  --contracts "${tmp_dir}/missing-owner.tsv" \
  --exceptions "${repo_root}/config/consistency/ddd4j-boot-contract-exceptions.tsv"

python3 "${validator}" \
  --branches "${repo_root}/config/consistency/ddd4j-boot-branch-groups.tsv" \
  --contracts "${repo_root}/config/consistency/ddd4j-boot-logical-contracts.tsv" \
  --exceptions "${repo_root}/config/consistency/ddd4j-boot-contract-exceptions.tsv"

echo "PASS: contract data validation"
