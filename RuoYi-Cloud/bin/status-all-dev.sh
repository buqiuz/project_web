#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
RUN_DIR="$PROJECT_ROOT/.run"

show_pid_status() {
  local name="$1"
  local pid_file="$RUN_DIR/${name}.pid"

  if [[ ! -f "$pid_file" ]]; then
    echo "$name: not tracked"
    return 0
  fi

  local pid
  pid="$(cat "$pid_file" 2>/dev/null || true)"
  if [[ -z "$pid" ]]; then
    rm -f "$pid_file"
    echo "$name: invalid pid file (cleaned)"
    return 0
  fi

  if kill -0 "$pid" 2>/dev/null; then
    echo "$name: running (pid: $pid)"
  else
    rm -f "$pid_file"
    echo "$name: stopped (stale pid cleaned: $pid)"
  fi
}

show_url_status() {
  local name="$1"
  local url="$2"

  if ! command -v curl >/dev/null 2>&1; then
    echo "$name: curl not found"
    return 0
  fi

  local code
  code="$(curl -s -o /dev/null -w '%{http_code}' --max-time 8 "$url" || true)"
  if [[ "$code" == "200" || "$code" == "302" || "$code" == "401" ]]; then
    echo "$name: $code ($url)"
  else
    echo "$name: FAIL(${code:-N/A}) ($url)"
  fi
}

echo "=== app processes ==="
show_pid_status "frontend"
show_pid_status "auth"
show_pid_status "system"
show_pid_status "gen"
show_pid_status "job"
show_pid_status "file"
show_pid_status "monitor"
show_pid_status "gateway"

echo
echo "=== middleware containers ==="
(
  cd "$PROJECT_ROOT/docker"
  docker compose ps ruoyi-mysql ruoyi-redis ruoyi-nacos ruoyi-sentinel
)

echo
echo "=== key urls ==="
show_url_status "gateway" "http://localhost:8080"
show_url_status "nacos" "http://localhost:8849/index.html"
show_url_status "sentinel" "http://localhost:8718/#/login"
