#!/usr/bin/env bash
set -euo pipefail

ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)
VERIFIER="$ROOT/scripts/consistency/verify_release_line_input.py"
TMP_DIR=$(mktemp -d)
trap 'rm -rf "$TMP_DIR"' EXIT

head -1 "$ROOT/config/consistency/ddd4j-boot-build-matrix.tsv" > "$TMP_DIR/matrix.tsv"
sed -n '2p' "$ROOT/config/consistency/ddd4j-boot-build-matrix.tsv" >> "$TMP_DIR/matrix.tsv"

cat > "$TMP_DIR/missing.tsv" <<'EOF'
branch	github_sha	codeup_sha	java	spring_boot	ddd4j	logical_state	report_ref
EOF
if python3 "$VERIFIER" --matrix "$TMP_DIR/matrix.tsv" --input "$TMP_DIR/missing.tsv" \
  --repository / --evidence-dir "$TMP_DIR" --skip-git >/dev/null 2>&1; then
  echo "FAIL: verifier accepted a missing release line" >&2
  exit 1
fi

cat > "$TMP_DIR/drift.tsv" <<'EOF'
branch	github_sha	codeup_sha	java	spring_boot	ddd4j	logical_state	report_ref
2.3.x	1111111111111111111111111111111111111111	2222222222222222222222222222222222222222	8	2.3.12.RELEASE	1.0.x.20260630-SNAPSHOT	PASS	report.md
EOF
if python3 "$VERIFIER" --matrix "$TMP_DIR/matrix.tsv" --input "$TMP_DIR/drift.tsv" \
  --repository / --evidence-dir "$TMP_DIR" --skip-git >/dev/null 2>&1; then
  echo "FAIL: verifier accepted divergent remote SHAs" >&2
  exit 1
fi

mkdir -p "$TMP_DIR/evidence"
cat > "$TMP_DIR/report.md" <<'EOF'
clean verify 111111111111
EOF
cat > "$TMP_DIR/evidence/ddd4j-boot-2.3.x-clean-verify-review3-final.log" <<'EOF'
[INFO] Reactor Summary for ddd4j-boot 2.3.x.20260630-SNAPSHOT:
[INFO] BUILD SUCCESS
EOF
cat > "$TMP_DIR/valid.tsv" <<EOF
branch	github_sha	codeup_sha	java	spring_boot	ddd4j	logical_state	report_ref
2.3.x	1111111111111111111111111111111111111111	1111111111111111111111111111111111111111	8	2.3.12.RELEASE	1.0.x.20260630-SNAPSHOT	PASS	$TMP_DIR/report.md
EOF
python3 "$VERIFIER" --matrix "$TMP_DIR/matrix.tsv" --input "$TMP_DIR/valid.tsv" \
  --repository / --evidence-dir "$TMP_DIR/evidence" --skip-git

echo "PASS: release-line input contract"
