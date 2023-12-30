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
SHUTDOWN_DELAY_MIN="0"

mainProcess() {
  setSudoKameHouseCommand "/usr/sbin/shutdown"
  ${SUDO_KAMEHOUSE_COMMAND} -P ${SHUTDOWN_DELAY_MIN}
}

parseArguments() {
  while getopts ":d:" OPT; do
    case $OPT in
    ("d")
      SHUTDOWN_DELAY_MIN=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

printHelpOptions() {
  addHelpOption "-d" "shutdown delay in minutes"
}

main "$@"
