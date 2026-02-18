#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Searching for tomcat process"
  netstat -nltp | grep ":${TOMCAT_PORT} " | grep java
  TOMCAT_PID=`netstat -nltp | grep ":${TOMCAT_PORT} " | grep java | awk '{print $7}' | cut -d '/' -f 1`
  if [ -z "${TOMCAT_PID}" ]; then
    log.info "Tomcat is not running"
    return
  fi
  log.info "Killing process ${COL_PURPLE}${TOMCAT_PID}"
  kill ${TOMCAT_PID}

  log.info "Waiting for for tomcat to exit..."
  MAX_WAIT=20
  COUNT=0
  while kill -0 ${TOMCAT_PID} 2>/dev/null && [ $COUNT -lt $MAX_WAIT ]; do
    sleep 1
    ((COUNT++))
  done

  if kill -0 ${TOMCAT_PID} 2>/dev/null; then
    log.warn "Tomcat did not stop gracefully after ${MAX_WAIT}s. Forcing kill."
    kill -9 ${TOMCAT_PID}
  else
    log.info "Tomcat stopped successfully."
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
