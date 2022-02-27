#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

LOG_PROCESS_TO_FILE=true
DEBUG_MODE=""

mainProcess() {
  tomcat-stop.sh
  tomcat-startup.sh "${DEBUG_MODE}"
}

parseArguments() {
  while getopts ":dh" OPT; do
    case $OPT in
    ("d")
      DEBUG_MODE="-d"
      ;;
    ("h")
      parseHelp
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
  echo -e "     ${COL_BLUE}-d${COL_NORMAL} debug. restart tomcat in debug mode"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
}

main "$@"
