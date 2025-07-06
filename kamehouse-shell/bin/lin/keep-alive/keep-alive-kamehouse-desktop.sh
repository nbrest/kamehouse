#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  SERVICE="kamehouse-desktop"
  SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/desktop/kamehouse-desktop.sh"
}

mainProcess() {
  checkKeepAliveScriptsEnabled
  ps aux | grep "kamehouse-desktop.py" | grep "python" | awk '{print $2}'
  local PID=`ps aux | grep "kamehouse-desktop.py" | grep "python" | awk '{print $2}'`
  log.info "PID: ${PID}"
  if [ -z "${PID}" ]; then
    log.error "${SERVICE} not running. Starting it now"
    ${SERVICE_STARTUP} &
  else
    log.info "${SERVICE} is currently running with pid ${COL_PURPLE}${PID}"
  fi
}

main "$@"
