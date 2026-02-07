#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/keep-alive/keep-alive-functions.sh

initScriptEnv() {
  PORT=443
  SERVICE="httpd"
  SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/httpd/httpd-restart.sh"
}

runKeepAlive() {
  netstat -ano | grep "LISTENING" | grep ":${PORT} " | tail -n 1
  PID=`netstat -ano | grep "LISTENING" | grep ":${PORT} " | tail -n 1 | awk '{print $5}'`
  if [[ -z "${PID}" ]]; then
    log.error "${SERVICE} not running. Starting it now"
    ${SERVICE_STARTUP} &
  else 
    log.info "${SERVICE} is currently running with pid ${COL_PURPLE}${PID}"
  fi
}

main "$@"
