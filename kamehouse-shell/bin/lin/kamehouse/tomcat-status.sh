#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

mainProcess() {
  log.info "Searching for tomcat process"
  netstat -nltp | grep ":${TOMCAT_PORT} " | grep java
  TOMCAT_PID=`netstat -nltp | grep ":${TOMCAT_PORT} " | grep java | awk '{print $7}' | cut -d '/' -f 1`
  if [ -z "${TOMCAT_PID}" ]; then
    log.info "Tomcat is not running"
  else
    log.info "Tomcat is currently running with pid ${COL_PURPLE}${TOMCAT_PID}${COL_DEFAULT_LOG} on port ${COL_PURPLE}${TOMCAT_PORT}"
  fi
}

parseArguments() {
  parseTomcatPort "$@"
}

setEnvFromArguments() {
  setEnvForTomcatPort
}

printHelpOptions() {
  printTomcatPortOption
}

main "$@"
