#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=true

mainProcess() {
  log.info "Searching for tomcat process"
  netstat -ano | grep "LISTENING" | grep "${TOMCAT_PORT}" | tail -n 1 
  TOMCAT_PID=`netstat -ano | grep "LISTENING" | grep "${TOMCAT_PORT}" | tail -n 1 | awk '{print $5}' | cut -d '/' -f 1`
  if [ -z ${TOMCAT_PID} ]; then
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
