#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
DEFAULT_ROOT=$(cd "$SCRIPT_DIR/../.." && pwd)
REPO="$DEFAULT_ROOT"
BRANCH=""
PROFILE=""
OUTPUT=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --repo) REPO=$2; shift 2 ;;
    --branch) BRANCH=$2; shift 2 ;;
    --profile) PROFILE=$2; shift 2 ;;
    --output) OUTPUT=$2; shift 2 ;;
    *) echo "Unknown argument: $1" >&2; exit 64 ;;
  esac
done

if [[ -z "$BRANCH" || -z "$PROFILE" || -z "$OUTPUT" ]]; then
  echo "Usage: $0 --branch <branch> --profile <profile> --output <json> [--repo <path>]" >&2
  exit 64
fi

MATRIX="$REPO/config/consistency/ddd4j-boot-build-matrix.tsv"
if [[ ! -f "$MATRIX" ]]; then
  echo "Missing maintenance matrix: $MATRIX" >&2
  exit 66
fi

matrix_row=$(awk -F '\t' -v branch="$BRANCH" 'NR > 1 && $1 == branch { print; exit }' "$MATRIX")
if [[ -z "$matrix_row" ]]; then
  echo "Unknown maintenance branch: $BRANCH" >&2
  exit 65
fi

current_branch=$(git -C "$REPO" branch --show-current)
if [[ "$current_branch" != "$BRANCH" ]]; then
  echo "Current branch $current_branch does not match requested $BRANCH" >&2
  exit 65
fi

jdk=$(printf '%s\n' "$matrix_row" | awk -F '\t' '{print $2}')
boot=$(printf '%s\n' "$matrix_row" | awk -F '\t' '{print $3}')
ddd4j=$(printf '%s\n' "$matrix_row" | awk -F '\t' '{print $4}')
docker_cmd=${INTEGRATION_DOCKER_CMD:-docker}

write_result() {
  local status=$1 reason=$2 exit_code=$3 tests=$4 failures=$5 errors=$6 skipped=$7 log_path=$8 images=$9 command=${10}
  mkdir -p "$(dirname "$OUTPUT")"
  STATUS="$status" REASON="$reason" EXIT_CODE="$exit_code" TESTS="$tests" \
  FAILURES="$failures" ERRORS="$errors" SKIPPED="$skipped" LOG_PATH="$log_path" \
  IMAGES="$images" COMMAND="$command" BRANCH_VALUE="$BRANCH" PROFILE_VALUE="$PROFILE" \
  JDK_VALUE="$jdk" BOOT_VALUE="$boot" DDD4J_VALUE="$ddd4j" python3 - "$OUTPUT" <<'PY'
import json
import os
import sys

images = [item for item in os.environ["IMAGES"].splitlines() if item]
result = {
    "branch": os.environ["BRANCH_VALUE"],
    "profile": os.environ["PROFILE_VALUE"],
    "jdk": os.environ["JDK_VALUE"],
    "spring_boot": os.environ["BOOT_VALUE"],
    "ddd4j": os.environ["DDD4J_VALUE"],
    "status": os.environ["STATUS"],
    "reason": os.environ["REASON"],
    "command": os.environ["COMMAND"],
    "exit_code": int(os.environ["EXIT_CODE"]),
    "log": os.environ["LOG_PATH"],
    "images": images,
    "tests": {
        "total": int(os.environ["TESTS"]),
        "failures": int(os.environ["FAILURES"]),
        "errors": int(os.environ["ERRORS"]),
        "skipped": int(os.environ["SKIPPED"]),
    },
}
with open(sys.argv[1], "w", encoding="utf-8") as target:
    json.dump(result, target, ensure_ascii=False, indent=2, sort_keys=True)
    target.write("\n")
PY
}

if ! "$docker_cmd" info >/dev/null 2>&1; then
  write_result "BLOCKED" "Docker runtime is unavailable" 0 0 0 0 0 "" "" ""
  exit 0
fi

candidate_modules=(
  ddd4j-boot-cache
  ddd4j-boot-data/ddd4j-boot-data-mybatis
  ddd4j-boot-mq/ddd4j-boot-mq-activemq
  ddd4j-boot-mq/ddd4j-boot-mq-kafka
  ddd4j-boot-mq/ddd4j-boot-mq-mqtt-mica
  ddd4j-boot-mq/ddd4j-boot-mq-nats
  ddd4j-boot-mq/ddd4j-boot-mq-pulsar
  ddd4j-boot-mq/ddd4j-boot-mq-rabbitmq
  ddd4j-boot-mq/ddd4j-boot-mq-redis-stream
  ddd4j-boot-mq/ddd4j-boot-mq-rocketmq
  ddd4j-boot-mq/ddd4j-boot-mq-sqs
)
modules=()
for module in "${candidate_modules[@]}"; do
  [[ -f "$REPO/$module/pom.xml" ]] && modules+=("$module")
done
if [[ ${#modules[@]} -eq 0 ]]; then
  write_result "BLOCKED" "No branch-compatible integration modules were found" 0 0 0 0 0 "" "" ""
  exit 0
fi

module_csv=$(IFS=,; echo "${modules[*]}")
log_dir=$(mktemp -d)
log_path="$log_dir/integration-contracts.log"
maven_cmd=${INTEGRATION_MAVEN_CMD:-$REPO/mvnw}
profile_args=()
if [[ -f "$REPO/pom.xml" ]] && grep -q "<id>$PROFILE</id>" "$REPO/pom.xml"; then
  profile_args=("-P$PROFILE")
fi
command="$maven_cmd -B -ntp ${profile_args[*]} -pl $module_csv -am -DskipTests=false -Dmaven.test.skip=false test"

for module in "${modules[@]}"; do
  find "$REPO/$module/target/surefire-reports" -name 'TEST-*.xml' -type f -delete 2>/dev/null || true
done

set +e
(cd "$REPO" && "$maven_cmd" -B -ntp "${profile_args[@]}" -pl "$module_csv" -am \
  -DskipTests=false -Dmaven.test.skip=false test) >"$log_path" 2>&1
exit_code=$?
set -e

stats=$(python3 - "$REPO" "${modules[@]}" <<'PY'
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

root = Path(sys.argv[1])
total = failures = errors = skipped = 0
for module in sys.argv[2:]:
    for report in (root / module / "target/surefire-reports").glob("TEST-*.xml"):
        suite = ET.parse(report).getroot()
        total += int(suite.attrib.get("tests", 0))
        failures += int(suite.attrib.get("failures", 0))
        errors += int(suite.attrib.get("errors", 0))
        skipped += int(suite.attrib.get("skipped", 0))
print(total, failures, errors, skipped)
PY
)
read -r tests failures errors skipped <<<"$stats"
images=$(rg -o 'DockerImageName\.parse\("[^"]+"\)' "${modules[@]/#/$REPO/}" \
  --glob '*.java' 2>/dev/null | sed 's/.*parse("\([^"]*\)").*/\1/' | sort -u || true)

if [[ $exit_code -ne 0 || $failures -ne 0 || $errors -ne 0 ]]; then
  status="FAIL"
  reason="Maven or integration tests failed"
elif [[ $tests -eq 0 ]]; then
  status="BLOCKED"
  reason="No integration tests were executed"
elif [[ $skipped -ne 0 ]]; then
  status="BLOCKED"
  reason="Integration tests were skipped"
else
  status="PASS"
  reason="All selected integration contracts executed"
fi

write_result "$status" "$reason" "$exit_code" "$tests" "$failures" "$errors" "$skipped" \
  "$log_path" "$images" "$command"

[[ "$status" == "FAIL" ]] && exit 1
exit 0
