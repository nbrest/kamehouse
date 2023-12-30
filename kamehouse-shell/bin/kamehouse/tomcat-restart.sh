#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 149
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 149
fi

LOG_PROCESS_TO_FILE=true
DEBUG_MODE=""

mainProcess() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/tomcat-stop.sh
  killRemainingTomcatProcess
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/tomcat-startup.sh "${DEBUG_MODE}"
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

parseArguments() {
  while getopts ":d" OPT; do
    case $OPT in
    ("d")
      DEBUG_MODE="-d"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done 
}

printHelpOptions() {
  addHelpOption "-d" "debug. restart tomcat in debug mode"
}

main "$@"
