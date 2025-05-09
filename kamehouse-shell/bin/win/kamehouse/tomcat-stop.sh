#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

mainProcess() {
  log.info "Searching for tomcat process"
  netstat -ano | grep "LISTENING" | grep ":${TOMCAT_PORT} " | tail -n 1 
  TOMCAT_PID=`netstat -ano | grep "LISTENING" | grep ":${TOMCAT_PORT} " | tail -n 1 | awk '{print $5}' | cut -d '/' -f 1`
  if [ -z "${TOMCAT_PID}" ]; then
    log.info "Tomcat is not running"
  else
    log.info "Killing process ${COL_PURPLE}${TOMCAT_PID}"
    cmd.exe "/c taskkill.exe /PID ${TOMCAT_PID} /F"
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
