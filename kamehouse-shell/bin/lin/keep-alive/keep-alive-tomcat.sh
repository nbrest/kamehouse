#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  PORT=9090
  SERVICE="tomcat"
  SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/tomcat-restart.sh"
}

mainProcess() {
  checkKeepAliveScriptsEnabled
  PID=`netstat -nltp | grep ":${PORT} " | grep java | awk '{print $7}' | cut -d '/' -f 1`
  if [ -z "${PID}" ]; then
    log.error "${SERVICE} not running. Starting it now"
    ${SERVICE_STARTUP} &
  else
    log.info "${SERVICE} is currently running with pid ${COL_PURPLE}${PID}"
  fi
}

main "$@"
