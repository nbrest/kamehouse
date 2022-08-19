#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

LOG_PROCESS_TO_FILE=false
DEFAULT_HTTPD_PORT=80
HTTPD_PORT=""

mainProcess() {
  log.info "Searching for apache httpd process"
  netstat -ano | grep "LISTENING" | grep "\[::\]:${HTTPD_PORT}" | tail -n 1
  HTTPD_PID=`netstat -ano | grep "LISTENING" | grep "\[::\]:${HTTPD_PORT}" | tail -n 1 | awk '{print $5}' | cut -d '/' -f 1`
  if [ -z ${HTTPD_PID} ]; then
    log.info "Apache httpd is not running"
  else
    log.info "Apache httpd is currently running with pid ${COL_PURPLE}${HTTPD_PID}${COL_DEFAULT_LOG} on port ${COL_PURPLE}${HTTPD_PORT}"
  fi  
}

parseArguments() {
  while getopts ":p:" OPT; do
    case $OPT in
    ("p")
      HTTPD_PORT=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done

  if [ -z "${HTTPD_PORT}" ]; then
    HTTPD_PORT=${DEFAULT_HTTPD_PORT}
  fi
}

printHelpOptions() {
  addHelpOption "-p" "httpd port. Default ${DEFAULT_HTTPD_PORT}"
}

main "$@"
