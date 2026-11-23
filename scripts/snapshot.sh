#!/usr/bin/env bash
# Save the filtered conditions report so two demo steps can be diffed.
#
#   scripts/snapshot.sh before
#   ... change something, restart ...
#   scripts/snapshot.sh after
#   diff snapshots/before.txt snapshots/after.txt
set -euo pipefail

LABEL="${1:?usage: scripts/snapshot.sh <label>}"
mkdir -p snapshots
"$(dirname "$0")/conditions.sh" "${2:-}" > "snapshots/$LABEL.txt"
echo "saved snapshots/$LABEL.txt"
