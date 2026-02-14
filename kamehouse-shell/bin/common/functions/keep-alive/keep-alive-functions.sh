KEEP_ALIVE_SERVICE=""
KEEP_ALIVE_SERVICE_STARTUP=""
KEEP_ALIVE_SERVICE_PID=""

mainProcess() {
  checkKeepAliveScriptsEnabled
  runKeepAlive
}

# exit the process if keep alive scripts are disabled in the configuration
checkKeepAliveScriptsEnabled() {
  if ${KEEP_ALIVE_SCRIPTS_DISABLED}; then
    log.info "keep alive scripts are disabled in kamehouse.cfg"
    exitProcess ${EXIT_PROCESS_CANCELLED}
  fi
}

# Override in scrips to execute custom keep alive logic that doesn't rely on pid check
runKeepAlive() {
  if ${IS_LINUX_HOST}; then
    setKeepAliveServicePidLin
  else
    setKeepAliveServicePidWin
  fi
  checkKeepAliveServicePid
}

# Override to set pid in linux servers
setKeepAliveServicePidLin() {
  KEEP_ALIVE_SERVICE_PID=""
}

# Override to set pid in windows servers
setKeepAliveServicePidWin() {
  KEEP_ALIVE_SERVICE_PID=""
}

# Override to run custom checks on keep alive service pid
checkKeepAliveServicePid() {
  if [[ -z "${KEEP_ALIVE_SERVICE_PID}" ]]; then
    log.error "${KEEP_ALIVE_SERVICE} not running. Starting it now"
    runKeepAlivePidNotFound
  else 
    log.info "${KEEP_ALIVE_SERVICE} is currently running with pid ${COL_PURPLE}${PKEEP_ALIVE_SERVICE_PID}"
    runKeepAlivePidFound
  fi
}

# Override to run custom logic when pid is not found
runKeepAlivePidNotFound() {
  ${KEEP_ALIVE_SERVICE_STARTUP} &
}

# Override to run custom logic when pid is found
runKeepAlivePidFound() {
  return 
}