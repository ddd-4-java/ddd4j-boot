#!/usr/bin/env bash

set -euo pipefail

repo_root=$(cd "$(dirname "$0")/../.." && pwd -P)

python3 "$repo_root/scripts/consistency/verify_maintenance_build_matrix.py" \
  --repo "$repo_root" \
  --matrix "$repo_root/config/consistency/ddd4j-boot-build-matrix.tsv" \
  --ref-prefix github/
