KAMEHOUSE_GROOT_PATH="${HOME}/programs/kamehouse-groot"
KAMEHOUSE_GROOT_VENV_PATH="${KAMEHOUSE_GROOT_PATH}/venv"
KAMEHOUSE_GROOT_PYTHON="${KAMEHOUSE_GROOT_VENV_PATH}/bin/python"
KAMEHOUSE_GROOT_PID_FILE="${KAMEHOUSE_GROOT_PATH}/run/kamehouse-groot.pid"
KAMEHOUSE_GROOT_LOG_DIR="${KAMEHOUSE_GROOT_PATH}/logs"
KAMEHOUSE_GROOT_STARTUP_LOG="${KAMEHOUSE_GROOT_LOG_DIR}/kamehouse-groot-startup.log"
KAMEHOUSE_GROOT_PID=""

KAMEHOUSE_GROOT_STOP_TIMEOUT=20

setKameHouseGrootPid() {
  KAMEHOUSE_GROOT_PID=""
  if [ -f "${KAMEHOUSE_GROOT_PID_FILE}" ]; then
    local PID=`cat "${KAMEHOUSE_GROOT_PID_FILE}"`
    if [ -n "${PID}" ] && kill -0 ${PID} 2>/dev/null; then
      KAMEHOUSE_GROOT_PID=${PID}
      return
    fi
  fi
  KAMEHOUSE_GROOT_PID=`ps aux | grep "python.*kamehouse_groot" | grep -v "grep" | awk '{print $2}' | head -n 1`
}

isKameHouseGrootRunning() {
  setKameHouseGrootPid
  [ -n "${KAMEHOUSE_GROOT_PID}" ]
}

checkKameHouseGrootInstallation() {
  if [ ! -x "${KAMEHOUSE_GROOT_PYTHON}" ]; then
    log.error "kamehouse-groot virtual environment not found at ${COL_PURPLE}${KAMEHOUSE_GROOT_VENV_PATH}"
    log.error "Run ${COL_PURPLE}install-kamehouse-groot.sh${COL_DEFAULT_LOG} to install it"
    exitProcess ${EXIT_ERROR}
  fi
}

startKameHouseGroot() {
  if isKameHouseGrootRunning; then
    log.info "kamehouse-groot is already running with pid ${COL_PURPLE}${KAMEHOUSE_GROOT_PID}"
    return
  fi
  checkKameHouseGrootInstallation
  log.info "Starting ${COL_PURPLE}kamehouse-groot${COL_DEFAULT_LOG}"
  mkdir -p "${KAMEHOUSE_GROOT_LOG_DIR}" "${KAMEHOUSE_GROOT_PATH}/run"

  nohup "${KAMEHOUSE_GROOT_PYTHON}" -m kamehouse_groot > "${KAMEHOUSE_GROOT_STARTUP_LOG}" 2>&1 &
  local PID=$!
  echo "${PID}" > "${KAMEHOUSE_GROOT_PID_FILE}"

  log.info "Waiting for kamehouse-groot to start..."
  sleep 3
  if kill -0 ${PID} 2>/dev/null; then
    log.info "kamehouse-groot started successfully with pid ${COL_PURPLE}${PID}"
    return
  fi
  log.error "kamehouse-groot failed to start. Check ${COL_PURPLE}${KAMEHOUSE_GROOT_STARTUP_LOG}"
  tail -n 50 "${KAMEHOUSE_GROOT_STARTUP_LOG}"
  rm -f "${KAMEHOUSE_GROOT_PID_FILE}"
  exitProcess ${EXIT_ERROR}
}

stopKameHouseGroot() {
  if ! isKameHouseGrootRunning; then
    log.info "kamehouse-groot is not running"
    rm -f "${KAMEHOUSE_GROOT_PID_FILE}"
    return
  fi
  log.info "Stopping kamehouse-groot with pid ${COL_PURPLE}${KAMEHOUSE_GROOT_PID}"
  kill ${KAMEHOUSE_GROOT_PID}

  local COUNT=0
  while kill -0 ${KAMEHOUSE_GROOT_PID} 2>/dev/null && [ ${COUNT} -lt ${KAMEHOUSE_GROOT_STOP_TIMEOUT} ]; do
    sleep 1
    ((COUNT++))
  done

  if kill -0 ${KAMEHOUSE_GROOT_PID} 2>/dev/null; then
    log.warn "kamehouse-groot did not stop gracefully after ${KAMEHOUSE_GROOT_STOP_TIMEOUT}s. Forcing kill."
    kill -9 ${KAMEHOUSE_GROOT_PID}
  else
    log.info "kamehouse-groot stopped successfully"
  fi
  rm -f "${KAMEHOUSE_GROOT_PID_FILE}"
}

printKameHouseGrootStatus() {
  if isKameHouseGrootRunning; then
    log.info "kamehouse-groot is running with pid ${COL_PURPLE}${KAMEHOUSE_GROOT_PID}"
  else
    log.info "kamehouse-groot is not running"
  fi
}
