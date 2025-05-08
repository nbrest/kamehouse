#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-java-home.sh --skip-override --log
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-userhome.sh
  TOMCAT_DIR="${HOME}/programs/apache-tomcat"
  TOMCAT_LOG=${TOMCAT_DIR}/logs/catalina.out
  cd ${TOMCAT_DIR}
  if ${IS_LINUX_HOST}; then
    startTomcatLinux
  else
    startTomcatWindows
  fi
}

initScriptEnv() {
  DEBUG_MODE=false
  TOMCAT_DIR=""
  TOMCAT_LOG=""
}

startTomcatLinux() {
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

startTomcatWindows() {
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
