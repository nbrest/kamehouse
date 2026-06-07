#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  DEBUG_MODE=false
  TOMCAT_DIR=""
  TOMCAT_LOG=""
}

mainProcessPre() {
  source ${HOME}/programs/kamehouse-shell/bin/deploy/set-java-home.sh --skip-override --log
  source ${HOME}/programs/kamehouse-shell/bin/deploy/set-userhome.sh
  TOMCAT_DIR="${HOME}/programs/apache-tomcat"
  TOMCAT_LOG=${TOMCAT_DIR}/logs/catalina.out
  cd ${TOMCAT_DIR}
}

mainProcessLin() {
  setupLinuxEnvironment

  if ${DEBUG_MODE}; then
    log.info "Starting tomcat ${TOMCAT_DIR} in debug mode"
    log.debug "${TOMCAT_DIR}/bin/catalina.sh jpda start | tee ${TOMCAT_LOG}"
    cd ${TOMCAT_DIR}
    ${TOMCAT_DIR}/bin/catalina.sh jpda start | tee ${TOMCAT_LOG}
  else
    log.info "Starting tomcat ${TOMCAT_DIR}"
    log.debug "${TOMCAT_DIR}/bin/startup.sh"
    cd ${TOMCAT_DIR} 
    ${TOMCAT_DIR}/bin/startup.sh
  fi
}

mainProcessWin() {
  if ${DEBUG_MODE}; then
    log.info "Starting tomcat ${TOMCAT_DIR} in debug mode"
    log.debug "powershell.exe -c \"Start-Process ./bin/catalina.bat -ArgumentList \\\"jpda start\\\"\" &"
    powershell.exe -c "Start-Process ./bin/catalina.bat -ArgumentList \"jpda start\"" &
  else
    log.info "Starting tomcat ${TOMCAT_DIR}"
    log.debug "powershell.exe -c \"Start-Process ./bin/startup.bat\" &"
    powershell.exe -c "Start-Process ./bin/startup.bat" &
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
        DEBUG_MODE=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-d" "debug. start tomcat in debug mode"
}

main "$@"
