#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

LOG_PROCESS_TO_FILE=true
DEFAULT_TOMCAT_PORT=9090
TOMCAT_PORT=""

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
  while getopts ":p:" OPT; do
    case $OPT in
    ("p")
      TOMCAT_PORT=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done

  if [ -z "${TOMCAT_PORT}" ]; then
    TOMCAT_PORT=${DEFAULT_TOMCAT_PORT}
  fi
}

printHelpOptions() {
  addHelpOption "-p" "tomcat port. Default ${DEFAULT_TOMCAT_PORT}"
}

main "$@"
