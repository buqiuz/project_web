#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
RUN_DIR="$PROJECT_ROOT/.run"

STOP_MIDDLEWARES=1
for arg in "$@"; do
  case "$arg" in
    --no-middlewares)
      STOP_MIDDLEWARES=0
      ;;
    *)
      echo "Unknown option: $arg"
      echo "Usage: $0 [--no-middlewares]"
      exit 1
      ;;
  esac
done

stop_by_pid_file() {
  local name="$1"
  local pid_file="$RUN_DIR/${name}.pid"

  if [[ ! -f "$pid_file" ]]; then
    echo "[SKIP] $name pid file not found"
    return 0
  fi

  local pid
  pid="$(cat "$pid_file" 2>/dev/null || true)"
  if [[ -z "$pid" ]]; then
    echo "[SKIP] $name pid file empty"
    rm -f "$pid_file"
    return 0
  fi

  if kill -0 "$pid" 2>/dev/null; then
    echo "[STOP] $name (pid: $pid)"
    kill "$pid" 2>/dev/null || true

    for _ in {1..10}; do
      if ! kill -0 "$pid" 2>/dev/null; then
        break
      fi
      sleep 1
    done

    if kill -0 "$pid" 2>/dev/null; then
      echo "[KILL] $name (pid: $pid)"
      kill -9 "$pid" 2>/dev/null || true
    fi
  else
    echo "[SKIP] $name process not running"
  fi

  rm -f "$pid_file"
}

stop_by_pid_file "frontend"
stop_by_pid_file "gateway"
stop_by_pid_file "monitor"
stop_by_pid_file "file"
stop_by_pid_file "job"
stop_by_pid_file "gen"
stop_by_pid_file "system"
stop_by_pid_file "auth"

if [[ "$STOP_MIDDLEWARES" -eq 1 ]]; then
  echo "[STEP] stopping middlewares"
  (
    cd "$PROJECT_ROOT/docker"
    docker compose stop ruoyi-sentinel ruoyi-nacos ruoyi-redis ruoyi-mysql
  )
fi

echo "[DONE] stop completed"
