#!/usr/bin/env bash
set -euo pipefail

ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)
VERIFIER="$ROOT/scripts/consistency/verify_same_group_compatibility.sh"
GENERATOR="$ROOT/scripts/consistency/generate_branch_surfaces.py"
TMP_DIR=$(mktemp -d)
trap 'rm -rf "$TMP_DIR"' EXIT

cat > "$TMP_DIR/map.tsv" <<'EOF'
from_group	to_group	contract_id	source_surface	target_surface	behavior_delta	validation_command
jdk8	jdk17	AUTO_CONFIGURATION	factories	imports	behavior stable	mvn test
EOF
cat > "$TMP_DIR/baseline.tsv" <<'EOF'
jdk_group	surface	value	deprecated
jdk17	config:ddd4j.enabled	true	false
EOF
printf 'jdk_group\tsurface\tvalue\tdeprecated\n' > "$TMP_DIR/removed.tsv"
cat > "$TMP_DIR/drift.tsv" <<'EOF'
jdk_group	surface	value	deprecated
jdk17	config:ddd4j.enabled	false	false
EOF
cat > "$TMP_DIR/deprecated.tsv" <<'EOF'
jdk_group	surface	value	deprecated
jdk17	config:ddd4j.enabled	false	true
EOF

if "$VERIFIER" --migration-map "$TMP_DIR/map.tsv" --baseline-surfaces "$TMP_DIR/baseline.tsv" \
  --candidate-surfaces "$TMP_DIR/removed.tsv" --output "$TMP_DIR/removed.json" >/dev/null 2>&1; then
  echo "FAIL: verifier accepted removed same-group surface" >&2
  exit 1
fi

if "$VERIFIER" --migration-map "$TMP_DIR/map.tsv" --baseline-surfaces "$TMP_DIR/baseline.tsv" \
  --candidate-surfaces "$TMP_DIR/drift.tsv" --output "$TMP_DIR/drift.json" >/dev/null 2>&1; then
  echo "FAIL: verifier accepted changed same-group surface" >&2
  exit 1
fi

set +e
"$VERIFIER" --migration-map "$TMP_DIR/map.tsv" --baseline-surfaces "$TMP_DIR/baseline.tsv" \
  --candidate-surfaces "$TMP_DIR/deprecated.tsv" --output "$TMP_DIR/deprecated.json" >/dev/null 2>&1
review_exit=$?
set -e
if [[ $review_exit -ne 2 ]]; then
  echo "FAIL: deprecated change did not return review-required exit 2" >&2
  exit 1
fi
rg -Fq '"status": "REVIEW_REQUIRED"' "$TMP_DIR/deprecated.json"

mkdir -p "$TMP_DIR/tree/src/main/java/io/ddd4j/example" \
  "$TMP_DIR/tree/src/main/resources/META-INF" \
  "$TMP_DIR/tree/ddd4j-boot-samples/demo/src/main/java/io/ddd4j/sample"
cat > "$TMP_DIR/tree/src/main/java/io/ddd4j/example/Demo.java" <<'EOF'
package io.ddd4j.example;
public class Demo {
    public String find(String id) { return id; }
}
EOF
cat > "$TMP_DIR/tree/ddd4j-boot-samples/demo/src/main/java/io/ddd4j/sample/SampleOnly.java" <<'EOF'
package io.ddd4j.sample;
public class SampleOnly { public void run() {} }
EOF
cat > "$TMP_DIR/tree/src/main/resources/META-INF/spring-configuration-metadata.json" <<'EOF'
{"properties":[{"name":"ddd4j.demo.enabled","type":"java.lang.Boolean","defaultValue":true}]}
EOF
python3 "$GENERATOR" --tree "$TMP_DIR/tree" --jdk-group jdk17 --output "$TMP_DIR/generated.tsv"
rg -Fq $'jdk17\ttype:io.ddd4j.example.Demo\tpresent\tfalse' "$TMP_DIR/generated.tsv"
rg -Fq $'jdk17\tmethod:io.ddd4j.example.Demo#find(String)\tString\tfalse' "$TMP_DIR/generated.tsv"
rg -Fq $'jdk17\tconfig:ddd4j.demo.enabled\tjava.lang.Boolean=true\tfalse' "$TMP_DIR/generated.tsv"
if rg -q 'SampleOnly' "$TMP_DIR/generated.tsv"; then
  echo "FAIL: sample source leaked into public compatibility surfaces" >&2
  exit 1
fi

echo "PASS: same-group compatibility gate"
