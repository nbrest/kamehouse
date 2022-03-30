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
source ${HOME}/.kamehouse/.shell/.cred

# Initial config
LOG_PROCESS_TO_FILE=true
OPERATION=stop

# Variables set by command line arguments
MODULE_SHORT=

mainProcess() {
  executeOperationInTomcatManager ${OPERATION} ${TOMCAT_PORT} ${MODULE_SHORT}
}

parseArguments() {
  while getopts ":hm:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
    ("m")
      MODULE_SHORT="$OPTARG"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-m (admin|media|tennisworld|testmodule|ui|vlcrc)${COL_NORMAL} module to ${OPERATION}"
}

main "$@"
