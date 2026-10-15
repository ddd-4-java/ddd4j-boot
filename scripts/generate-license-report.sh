#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LICENSE_DIR="${DDD4J_REPORT_DIR:-${ROOT_DIR}/target/release-quality}/licenses"
cd "${ROOT_DIR}"
if [[ -e "${ROOT_DIR}/LICENSE-3RD-PARTY" ]]; then
  echo "Refusing to overwrite existing LICENSE-3RD-PARTY" >&2
  exit 1
fi
rm -rf "${LICENSE_DIR}" && mkdir -p "${LICENSE_DIR}"
./mvnw -B -ntp -DskipTests org.codehaus.mojo:license-maven-plugin:2.5.0:aggregate-add-third-party -Dlicense.outputDirectory="${LICENSE_DIR}" -Dlicense.thirdPartyFilename="THIRD-PARTY.txt"
if [[ -s "${ROOT_DIR}/LICENSE-3RD-PARTY" ]]; then
  mv "${ROOT_DIR}/LICENSE-3RD-PARTY" "${LICENSE_DIR}/THIRD-PARTY.txt"
fi
test -s "${LICENSE_DIR}/THIRD-PARTY.txt"
echo "[PASS] license inventory generated in ${LICENSE_DIR}/THIRD-PARTY.txt"
