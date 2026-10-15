#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SBOM_DIR="${DDD4J_REPORT_DIR:-${ROOT_DIR}/target/release-quality}/sbom"
SBOM_FILE="${SBOM_DIR}/ddd4j-sbom.json"
cd "${ROOT_DIR}"
rm -rf "${SBOM_DIR}"
mkdir -p "${SBOM_DIR}"

# CycloneDX's aggregate goal works on some Maven lines. Validate its graph
# before accepting it; a nonempty file can still contain zero components.
if ./mvnw -B -ntp -DskipTests -Dquarkus.build.skip=true \
  org.cyclonedx:cyclonedx-maven-plugin:2.9.3:makeAggregateBom \
  -DoutputFormat=all -DoutputName=ddd4j-sbom -DoutputDirectory="${SBOM_DIR}" &&
  python3 scripts/convert-dependency-tree-sbom.py --validate "${SBOM_FILE}"; then
  echo "[PASS] CycloneDX aggregate SBOM generated in ${SBOM_FILE}"
  exit 0
fi

echo "[INFO] Aggregate SBOM unavailable; resolving publishable reactor dependency trees"
find "${ROOT_DIR}" -type f -path '*/target/ddd4j-dependency-tree.json' -delete
MODULES="$(python3 scripts/convert-dependency-tree-sbom.py --root "${ROOT_DIR}" --modules)"
./mvnw -B -ntp -DskipTests -Dquarkus.build.skip=true \
  -pl "${MODULES}" -am \
  org.apache.maven.plugins:maven-dependency-plugin:3.10.0:tree \
  -DoutputType=json -DoutputFile=target/ddd4j-dependency-tree.json
python3 scripts/convert-dependency-tree-sbom.py \
  --root "${ROOT_DIR}" --output "${SBOM_FILE}"
test -s "${SBOM_FILE}"
echo "[PASS] Resolved reactor SBOM generated in ${SBOM_FILE}"
