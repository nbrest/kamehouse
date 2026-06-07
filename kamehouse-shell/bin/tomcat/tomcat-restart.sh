#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  DEBUG_MODE=""
  TOMCAT_PORT=9005
}

mainProcessPre() {
  ${HOME}/programs/kamehouse-shell/bin/tomcat/tomcat-stop.sh
  # check for processes running on port 9005 and 9090 and kill them
  log.info "Killing remaining tomcat processes"
  log.info "Searching for tomcat process"
}

mainProcessPost() {
  ${HOME}/programs/kamehouse-shell/bin/tomcat/tomcat-startup.sh "${DEBUG_MODE}"
}

mainProcessLin() {
  netstat -nltp | grep ":${TOMCAT_PORT} " | grep java
  TOMCAT_PID=`netstat -nltp | grep ":${TOMCAT_PORT} " | grep java | awk '{print $7}' | cut -d '/' -f 1`
  if [ -z "${TOMCAT_PID}" ]; then
    log.info "Tomcat is not running on port ${TOMCAT_PORT}"
  else
    log.info "Killing process ${COL_PURPLE}${TOMCAT_PID}"
    kill -9 ${TOMCAT_PID}
  fi
}

mainProcessWin() {
  netstat -ano | grep "LISTENING" | grep ":${TOMCAT_PORT} " | tail -n 1 
  TOMCAT_PID=`netstat -ano | grep "LISTENING" | grep ":${TOMCAT_PORT} " | tail -n 1 | awk '{print $5}' | cut -d '/' -f 1`
  if [ -z "${TOMCAT_PID}" ]; then
    log.info "Tomcat is not running on port ${TOMCAT_PORT}"
  else
    log.info "Killing process ${COL_PURPLE}${TOMCAT_PID}"
    powershell.exe -c "taskkill.exe /PID ${TOMCAT_PID} /F"
    powershell.exe -c "Stop-Process -Id ${TOMCAT_PID} -Force"
  fi
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -d)
        DEBUG_MODE="-d"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-d" "debug. restart tomcat in debug mode"
}

main "$@"
