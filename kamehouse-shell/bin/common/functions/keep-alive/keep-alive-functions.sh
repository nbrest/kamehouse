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

# Override in scrips to execute keep alive logic
runKeepAlive() {
  return
}
