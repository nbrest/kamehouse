#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

LOG_PROCESS_TO_FILE=true
DEFAULT_HTTPD_PORT=80
HTTPD_PORT=""

mainProcess() {
  log.info "Searching for apache httpd process"
  log.warn "User running this script needs ${COL_RED}sudo netstat${COL_DEFAULT_LOG} permissions"
  sudo netstat -nltp | grep ${HTTPD_PORT} | grep apache 
  HTTPD_PID=`sudo netstat -nltp | grep ${HTTPD_PORT} | grep apache | awk '{print $7}' | cut -d '/' -f 1`
  if [ -z ${HTTPD_PID} ]; then
    log.info "Apache httpd is not running"
  else
    log.info "Apache httpd is currently running with pid ${COL_PURPLE}${HTTPD_PID}${COL_DEFAULT_LOG} on port ${COL_PURPLE}${HTTPD_PORT}"
  fi
}

parseArguments() {
  while getopts ":hp:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
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

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-p${COL_NORMAL} httpd port. Default ${DEFAULT_HTTPD_PORT}" 
}

main "$@"
