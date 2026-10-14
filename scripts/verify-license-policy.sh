#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
REPORT_DIR="${DDD4J_REPORT_DIR:-${ROOT_DIR}/target/release-quality}"
LICENSE_FILE="${REPORT_DIR}/licenses/THIRD-PARTY.txt"
SBOM_FILE="${REPORT_DIR}/sbom/ddd4j-sbom.json"
VIOLATIONS_FILE="${REPORT_DIR}/licenses/THIRD-PARTY-policy-violations.txt"

cd "${ROOT_DIR}"
if [[ ! -s "${LICENSE_FILE}" ]]; then
  ./scripts/generate-license-report.sh
fi
if [[ ! -s "${SBOM_FILE}" ]]; then
  ./scripts/generate-sbom.sh
fi

python3 scripts/check-license-policy.py \
  --report "${LICENSE_FILE}" \
  --sbom "${SBOM_FILE}" \
  --selections "${ROOT_DIR}/config/license-selections.tsv" \
  --build-tool-exclusions "${ROOT_DIR}/config/license-build-tool-exclusions.tsv" \
  --violations "${VIOLATIONS_FILE}"
