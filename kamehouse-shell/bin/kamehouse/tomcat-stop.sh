#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

LOG_PROCESS_TO_FILE=false

main() {
  if ${IS_LINUX_HOST}; then
    ${HOME}/programs/kamehouse-shell/bin/lin/kamehouse/tomcat-stop.sh "$@"
  else
    ${HOME}/programs/kamehouse-shell/bin/win/kamehouse/tomcat-stop.sh "$@"
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-p ${COL_NORMAL} tomcat port. Default ${DEFAULT_TOMCAT_PORT}" 
}

main "$@"
