#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/tomcat-stop-dev.sh
  killRemainingTomcatProcess
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/tomcat-startup-dev.sh
}

killRemainingTomcatProcess() {
  # check for processes running on port 9005 and 9090 and kill them
  log.info "Killing remaining tomcat processes"
  log.info "Searching for tomcat process"
  local TOMCAT_PORT=9005
  if ${IS_LINUX_HOST}; then
    netstat -nltp | grep ":${TOMCAT_PORT} " | grep java
    TOMCAT_PID=`netstat -nltp | grep ":${TOMCAT_PORT} " | grep java | awk '{print $7}' | cut -d '/' -f 1`
    if [ -z "${TOMCAT_PID}" ]; then
      log.info "Tomcat is not running on port ${TOMCAT_PORT}"
    else
      log.info "Killing process ${COL_PURPLE}${TOMCAT_PID}"
      kill -9 ${TOMCAT_PID}
    fi
  else
    netstat -ano | grep "LISTENING" | grep ":${TOMCAT_PORT} " | tail -n 1 
    TOMCAT_PID=`netstat -ano | grep "LISTENING" | grep ":${TOMCAT_PORT} " | tail -n 1 | awk '{print $5}' | cut -d '/' -f 1`
    if [ -z "${TOMCAT_PID}" ]; then
      log.info "Tomcat is not running on port ${TOMCAT_PORT}"
    else
      log.info "Killing process ${COL_PURPLE}${TOMCAT_PID}"
      cmd.exe "/c taskkill.exe /PID ${TOMCAT_PID} /F"
    fi
  fi
}

main "$@"
