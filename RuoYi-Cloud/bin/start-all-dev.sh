#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
WORKSPACE_ROOT="$(cd "$PROJECT_ROOT/.." && pwd)"
FRONTEND_ROOT="$WORKSPACE_ROOT/RuoYi-Cloud-Vue3"

RUN_DIR="$PROJECT_ROOT/.run"
LOG_DIR="$PROJECT_ROOT/.run-logs"
mkdir -p "$RUN_DIR" "$LOG_DIR"

START_MIDDLEWARES=1
START_FRONTEND=1
START_BUILD=1
START_MENU_FIX=1

for arg in "$@"; do
  case "$arg" in
    --no-middlewares)
      START_MIDDLEWARES=0
      ;;
    --no-frontend)
      START_FRONTEND=0
      ;;
    --skip-build)
      START_BUILD=0
      ;;
    --no-menu-fix)
      START_MENU_FIX=0
      ;;
    *)
      echo "Unknown option: $arg"
      echo "Usage: $0 [--no-middlewares] [--no-frontend] [--skip-build] [--no-menu-fix]"
      exit 1
      ;;
  esac
done

apply_console_menu_fixes() {
  local sql
  sql="
update sys_menu
set path='http://localhost:8718', is_frame=0, component='', route_name='', update_by='admin', update_time=sysdate(), remark='Sentinel控制台（8718）'
where menu_id=111;

update sys_menu
set path='http://localhost:8849/index.html', is_frame=0, component='', route_name='', update_by='admin', update_time=sysdate(), remark='Nacos控制台（v3控制台端口 8849）'
where menu_id=112;
"

  echo "[STEP] applying console menu fixes"
  for _ in {1..20}; do
    if (
      cd "$PROJECT_ROOT/docker"
      docker compose exec -T ruoyi-mysql mysql -uroot -ppassword -D ry-cloud -e "$sql" >/dev/null 2>&1
    ); then
      echo "[OK] console menu fixes applied"
      return 0
    fi
    sleep 2
  done

  echo "[WARN] could not apply menu fixes now (mysql may be unavailable)."
  echo "       you can rerun this script later or run SQL manually."
}

check_url() {
  local name="$1"
  local url="$2"
  local code=""

  if ! command -v curl >/dev/null 2>&1; then
    echo "[SKIP] curl not found, skip $name check"
    return 0
  fi

  code="$(curl -s -o /dev/null -w '%{http_code}' --max-time 8 "$url" || true)"
  if [[ "$code" == "200" || "$code" == "302" || "$code" == "401" ]]; then
    echo "[OK] $name => $code ($url)"
  else
    echo "[WARN] $name => ${code:-N/A} ($url)"
  fi
}

print_access_summary() {
  echo
  echo "=== Access Summary ==="
  echo "Frontend(default):   http://localhost:80"
  echo "Gateway:             http://localhost:8080"
  echo "Nacos:               http://localhost:8849/index.html"
  echo "Sentinel:            http://localhost:8718/#/login"
  echo "Monitor(Admin):      http://localhost:9100/login"
  echo
}

cleanup_stale_pid() {
  local pid_file="$1"
  if [[ ! -f "$pid_file" ]]; then
    return 0
  fi

  local pid
  pid="$(cat "$pid_file" 2>/dev/null || true)"
  if [[ -z "$pid" ]]; then
    rm -f "$pid_file"
    return 0
  fi

  if ! kill -0 "$pid" 2>/dev/null; then
    rm -f "$pid_file"
  fi
}

is_pid_running() {
  local pid_file="$1"
  if [[ ! -f "$pid_file" ]]; then
    return 1
  fi

  local pid
  pid="$(cat "$pid_file" 2>/dev/null || true)"
  if [[ -z "$pid" ]]; then
    return 1
  fi

  kill -0 "$pid" 2>/dev/null
}

start_service() {
  local name="$1"
  local pom_path="$2"
  local pid_file="$RUN_DIR/${name}.pid"
  local log_file="$LOG_DIR/${name}.log"

  cleanup_stale_pid "$pid_file"

  if is_pid_running "$pid_file"; then
    echo "[SKIP] $name already running (pid: $(cat "$pid_file"))"
    return 0
  fi

  echo "[START] $name"
  nohup mvn -DskipTests -f "$pom_path" spring-boot:run >"$log_file" 2>&1 &
  echo "$!" >"$pid_file"

  for _ in {1..20}; do
    if ! is_pid_running "$pid_file"; then
      echo "[FAIL] $name exited during startup. Check: $log_file"
      tail -n 60 "$log_file" || true
      rm -f "$pid_file"
      exit 1
    fi

    if grep -q "BUILD FAILURE\|Could not resolve dependencies\|Unable to find a suitable main class" "$log_file"; then
      echo "[FAIL] $name failed during Maven startup. Check: $log_file"
      tail -n 60 "$log_file" || true
      rm -f "$pid_file"
      exit 1
    fi

    sleep 1
  done

  echo "[OK] $name started (pid: $(cat "$pid_file"), log: $log_file)"
}

start_frontend() {
  local pid_file="$RUN_DIR/frontend.pid"
  local log_file="$LOG_DIR/frontend.log"

  cleanup_stale_pid "$pid_file"

  if [[ ! -d "$FRONTEND_ROOT" ]]; then
    echo "[SKIP] frontend directory not found: $FRONTEND_ROOT"
    return 0
  fi

  if is_pid_running "$pid_file"; then
    echo "[SKIP] frontend already running (pid: $(cat "$pid_file"))"
    return 0
  fi

  echo "[START] frontend"
  nohup bash -lc "cd '$FRONTEND_ROOT' && yarn dev" >"$log_file" 2>&1 &
  echo "$!" >"$pid_file"

  for _ in {1..8}; do
    if ! is_pid_running "$pid_file"; then
      echo "[FAIL] frontend exited during startup. Check: $log_file"
      tail -n 60 "$log_file" || true
      rm -f "$pid_file"
      exit 1
    fi
    sleep 1
  done

  echo "[OK] frontend started (pid: $(cat "$pid_file"), log: $log_file)"
}

prebuild_shared_modules() {
  local log_file="$LOG_DIR/prebuild.log"
  local modules="ruoyi-auth,ruoyi-gateway,ruoyi-modules/ruoyi-system,ruoyi-modules/ruoyi-gen,ruoyi-modules/ruoyi-job,ruoyi-modules/ruoyi-file,ruoyi-visual/ruoyi-monitor"

  echo "[STEP] prebuilding shared modules"
  if ! mvn -f "$PROJECT_ROOT/pom.xml" -DskipTests -pl "$modules" -am install >"$log_file" 2>&1; then
    echo "[FAIL] prebuild failed. Check: $log_file"
    tail -n 80 "$log_file" || true
    exit 1
  fi

  echo "[OK] prebuild completed (log: $log_file)"
}

if [[ "$START_MIDDLEWARES" -eq 1 ]]; then
  echo "[STEP] starting middlewares"
  (
    cd "$PROJECT_ROOT/docker"
    docker compose up -d ruoyi-mysql ruoyi-redis ruoyi-nacos ruoyi-sentinel
  )
fi

if [[ "$START_MENU_FIX" -eq 1 ]]; then
  apply_console_menu_fixes
fi

if [[ "$START_BUILD" -eq 1 ]]; then
  prebuild_shared_modules
fi

echo "[STEP] starting backend services"
start_service "auth" "$PROJECT_ROOT/ruoyi-auth/pom.xml"
start_service "system" "$PROJECT_ROOT/ruoyi-modules/ruoyi-system/pom.xml"
start_service "gen" "$PROJECT_ROOT/ruoyi-modules/ruoyi-gen/pom.xml"
start_service "job" "$PROJECT_ROOT/ruoyi-modules/ruoyi-job/pom.xml"
start_service "file" "$PROJECT_ROOT/ruoyi-modules/ruoyi-file/pom.xml"
start_service "monitor" "$PROJECT_ROOT/ruoyi-visual/ruoyi-monitor/pom.xml"
start_service "gateway" "$PROJECT_ROOT/ruoyi-gateway/pom.xml"

if [[ "$START_FRONTEND" -eq 1 ]]; then
  echo "[STEP] starting frontend"
  start_frontend
fi

print_access_summary
check_url "Sentinel" "http://localhost:8718/#/login"
check_url "Nacos" "http://localhost:8849/index.html"

echo "[DONE] all requested services have been started"
