#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/keep-alive/keep-alive-functions.sh

initScriptEnv() {
  PORT=9090
  SERVICE="tomcat"
  SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/tomcat/tomcat-restart.sh"
}

runKeepAlive() {
  PID=`netstat -nltp | grep ":${PORT} " | grep java | awk '{print $7}' | cut -d '/' -f 1`
  if [ -z "${PID}" ]; then
    log.error "${SERVICE} not running. Starting it now"
    ${SERVICE_STARTUP} &
  else
    log.info "${SERVICE} is currently running with pid ${COL_PURPLE}${PID}"
  fi
}

main "$@"
