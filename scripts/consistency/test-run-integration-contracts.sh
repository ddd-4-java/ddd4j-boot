#!/usr/bin/env bash
set -euo pipefail

ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)
RUNNER="$ROOT/scripts/consistency/run_integration_contracts.sh"
TMP_DIR=$(mktemp -d)
trap 'rm -rf "$TMP_DIR"' EXIT

if "$RUNNER" --profile integration --output "$TMP_DIR/missing-branch.json" >/dev/null 2>&1; then
  echo "FAIL: runner accepted missing branch metadata" >&2
  exit 1
fi

if "$RUNNER" --branch 4.1.x --output "$TMP_DIR/missing-profile.json" >/dev/null 2>&1; then
  echo "FAIL: runner accepted missing profile metadata" >&2
  exit 1
fi

cat > "$TMP_DIR/docker-unavailable" <<'EOF'
#!/usr/bin/env bash
exit 1
EOF
chmod +x "$TMP_DIR/docker-unavailable"

INTEGRATION_DOCKER_CMD="$TMP_DIR/docker-unavailable" \
  "$RUNNER" --branch 4.1.x --profile integration \
  --output "$TMP_DIR/blocked.json" >/dev/null

python3 - "$TMP_DIR/blocked.json" <<'PY'
import json
import sys

result = json.load(open(sys.argv[1], encoding="utf-8"))
assert result["status"] == "BLOCKED", result
assert "Docker" in result["reason"], result
PY

cat > "$TMP_DIR/docker-available" <<'EOF'
#!/usr/bin/env bash
exit 0
EOF
chmod +x "$TMP_DIR/docker-available"

cat > "$TMP_DIR/maven-skipped" <<'EOF'
#!/usr/bin/env bash
mkdir -p ddd4j-boot-mq/ddd4j-boot-mq-nats/target/surefire-reports
cat > ddd4j-boot-mq/ddd4j-boot-mq-nats/target/surefire-reports/TEST-skipped.xml <<'XML'
<testsuite tests="1" failures="0" errors="0" skipped="1"/>
XML
exit 0
EOF
chmod +x "$TMP_DIR/maven-skipped"

fixture_repo="$TMP_DIR/repo"
mkdir -p "$fixture_repo/config/consistency" "$fixture_repo/ddd4j-boot-mq/ddd4j-boot-mq-nats"
cp "$ROOT/config/consistency/ddd4j-boot-build-matrix.tsv" "$fixture_repo/config/consistency/"
printf '%s\n' '<project />' > "$fixture_repo/ddd4j-boot-mq/ddd4j-boot-mq-nats/pom.xml"
git -C "$fixture_repo" init -q
git -C "$fixture_repo" config user.email test@example.invalid
git -C "$fixture_repo" config user.name Test
git -C "$fixture_repo" checkout -qb 4.1.x

INTEGRATION_DOCKER_CMD="$TMP_DIR/docker-available" \
INTEGRATION_MAVEN_CMD="$TMP_DIR/maven-skipped" \
  "$RUNNER" --repo "$fixture_repo" --branch 4.1.x --profile integration \
  --output "$TMP_DIR/skipped.json" >/dev/null

python3 - "$TMP_DIR/skipped.json" <<'PY'
import json
import sys

result = json.load(open(sys.argv[1], encoding="utf-8"))
assert result["status"] == "BLOCKED", result
assert result["tests"]["skipped"] == 1, result
assert "-Pintegration" not in result["command"], result
PY

echo "PASS: integration contract runner classification"
