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
  HTTPD_PORT_PARAM=$1
  if [ -z "${HTTPD_PORT_PARAM}" ]; then
    HTTPD_PORT=${DEFAULT_HTTPD_PORT}
  else
    HTTPD_PORT=${HTTPD_PORT_PARAM}
  fi

  log.info "Searching for apache httpd process"
  sudo netstat -nltp | grep ${HTTPD_PORT} | grep apache 
  HTTPD_PID=`sudo netstat -nltp | grep ${HTTPD_PORT} | grep apache | awk '{print $7}' | cut -d '/' -f 1`
  if [ -z ${HTTPD_PID} ]; then
    log.info "Apache httpd is not running"
  else
    log.info "Apache httpd is currently running with pid ${COL_PURPLE}${HTTPD_PID}${COL_DEFAULT_LOG} on port ${COL_PURPLE}${HTTPD_PORT}"
  fi
}

main "$@"
