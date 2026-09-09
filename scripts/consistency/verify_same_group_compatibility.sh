#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
COMPARATOR="$SCRIPT_DIR/compare_branch_contracts.py"
ROOT=$(cd "$SCRIPT_DIR/../.." && pwd)

if [[ $# -eq 0 ]]; then
  TMP_DIR=$(mktemp -d)
  trap 'rm -rf "$TMP_DIR"' EXIT
  BRANCH_GROUP_FILE="$ROOT/config/consistency/ddd4j-boot-branch-groups.tsv"
  MAP="$ROOT/config/consistency/ddd4j-boot-migration-map.tsv"
  while IFS=$'\t' read -r branch group _; do
    [[ "$branch" == "branch" || -z "$branch" ]] && continue
    tree="$TMP_DIR/tree-$branch"
    mkdir -p "$tree"
    git -C "$ROOT" archive "github/$branch" | tar -x -C "$tree"
    python3 "$SCRIPT_DIR/generate_branch_surfaces.py" \
      --tree "$tree" --jdk-group "$group" --output "$TMP_DIR/$branch.tsv"
    baseline_file="$TMP_DIR/baseline-$group"
    if [[ ! -f "$baseline_file" ]]; then
      printf '%s\n' "$branch" > "$baseline_file"
      continue
    fi
    baseline=$(cat "$baseline_file")
    "$0" --migration-map "$MAP" \
      --baseline-surfaces "$TMP_DIR/$baseline.tsv" \
      --candidate-surfaces "$TMP_DIR/$branch.tsv" \
      --output "$TMP_DIR/$baseline--$branch.json"
    printf 'PASS: %s -> %s (%s)\n' "$baseline" "$branch" "$group"
  done < "$BRANCH_GROUP_FILE"
  exit 0
fi

MIGRATION_MAP=""
BASELINE=""
CANDIDATE=""
OUTPUT=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --migration-map) MIGRATION_MAP=$2; shift 2 ;;
    --baseline-surfaces) BASELINE=$2; shift 2 ;;
    --candidate-surfaces) CANDIDATE=$2; shift 2 ;;
    --output) OUTPUT=$2; shift 2 ;;
    *) echo "Unknown argument: $1" >&2; exit 64 ;;
  esac
done

if [[ -z "$MIGRATION_MAP" || -z "$BASELINE" || -z "$CANDIDATE" || -z "$OUTPUT" ]]; then
  echo "Usage: $0 --migration-map <tsv> --baseline-surfaces <tsv> --candidate-surfaces <tsv> --output <json>" >&2
  exit 64
fi

python3 "$COMPARATOR" \
  --migration-map "$MIGRATION_MAP" \
  --baseline-surfaces "$BASELINE" \
  --candidate-surfaces "$CANDIDATE" \
  --output "$OUTPUT"

status=$(python3 - "$OUTPUT" <<'PY'
import json
import sys
print(json.load(open(sys.argv[1], encoding="utf-8"))["status"])
PY
)

if [[ "$status" == "REVIEW_REQUIRED" ]]; then
  exit 2
fi
[[ "$status" == "PASS" ]]
