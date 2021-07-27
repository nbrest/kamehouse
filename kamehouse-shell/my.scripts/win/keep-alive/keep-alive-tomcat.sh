#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=true

mainProcess() {
  TOMCAT_STATUS=`netstat -ano | grep "LISTENING" | grep "9090" | tail -n 1`
  if [[ -z ${TOMCAT_STATUS} ]]; then
    log.info "Tomcat not running. Starting it now"
    ${HOME}/my.scripts/kamehouse/tomcat-startup.sh &
  else 
    log.info "Tomcat is already running"
  fi
}

main "$@"
