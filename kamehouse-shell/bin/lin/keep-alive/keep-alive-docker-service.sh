#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 9
fi

# Global variables
LOG_PROCESS_TO_FILE=true
#PORT=9091
SERVICE="docker"
SERVICE_STARTUP="sudo service docker start"

mainProcess() {
  log.warn "User running this script needs ${COL_RED}sudo service${COL_DEFAULT_LOG} permissions"
  ${SERVICE_STARTUP} &
}

main "$@"
