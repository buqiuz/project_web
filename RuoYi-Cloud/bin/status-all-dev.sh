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
  docker compose ps ruoyi-mysql ruoyi-redis ruoyi-nacos
)
