KEEP_ALIVE_SERVICE=""
KEEP_ALIVE_SERVICE_STARTUP=""
KEEP_ALIVE_SERVICE_PID=""

mainProcessPre() {
  checkKeepAliveScriptsEnabled
}

mainProcessPost() {
  checkKeepAliveServicePid
}

# exit the process if keep alive scripts are disabled in the configuration
checkKeepAliveScriptsEnabled() {
  if ${KEEP_ALIVE_SCRIPTS_DISABLED}; then
    log.info "keep alive scripts are disabled in kamehouse.cfg"
    exitProcess ${EXIT_PROCESS_CANCELLED}
  fi
}

# Override to set pid in linux servers
mainProcessLin() {
  KEEP_ALIVE_SERVICE_PID=""
}

# Override to set pid in windows servers
mainProcessWin() {
  KEEP_ALIVE_SERVICE_PID=""
}

# Override to run custom checks on keep alive service pid
checkKeepAliveServicePid() {
  if [[ -z "${KEEP_ALIVE_SERVICE_PID}" ]]; then
    log.error "${KEEP_ALIVE_SERVICE} not running. Starting it now"
    runKeepAliveServicePidNotFound
  else 
    log.info "${KEEP_ALIVE_SERVICE} is currently running with pid ${COL_PURPLE}${KEEP_ALIVE_SERVICE_PID}"
    runKeepAliveServicePidFound
  fi
}

# Override to run custom logic when pid is not found
runKeepAliveServicePidNotFound() {
  ${KEEP_ALIVE_SERVICE_STARTUP} &
}

# Override to run custom logic when pid is found
runKeepAliveServicePidFound() {
  return 
}