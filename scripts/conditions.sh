#!/usr/bin/env bash
# Print the Conditions Evaluation Report entries whose name matches a pattern.
#
#   scripts/conditions.sh                 # the beans this demo cares about
#   scripts/conditions.sh 'Jackson|Hikari'  # any regex
#
# Env: BASE_URL (default http://localhost:8080), CURL_AUTH ("user:password"
# once Spring Security protects the actuator endpoints).
set -euo pipefail

PATTERN="${1:-Jackson|DataSource|Tomcat|Jetty|WebMvcAutoConfiguration|Hibernate|SecurityFilterChain|UserDetailsService}"
BASE_URL="${BASE_URL:-http://localhost:8080}"
AUTH=()
if [[ -n "${CURL_AUTH:-}" ]]; then AUTH=(-u "$CURL_AUTH"); fi

curl -sf ${AUTH[@]+"${AUTH[@]}"} "$BASE_URL/actuator/conditions" | PATTERN="$PATTERN" python3 -c '
import json, os, re, sys
rx = re.compile(os.environ["PATTERN"])
ctx = next(iter(json.load(sys.stdin)["contexts"].values()))

print("== POSITIVE matches (default registered)")
for name, conds in sorted(ctx["positiveMatches"].items()):
    if rx.search(name):
        print("  + " + name)
        for c in conds:
            print("      " + c["message"].replace("\n", " "))

print("== NEGATIVE matches (default backed off)")
for name, body in sorted(ctx["negativeMatches"].items()):
    if rx.search(name):
        print("  - " + name)
        for c in body["notMatched"]:
            print("      " + c["message"].replace("\n", " "))
'
