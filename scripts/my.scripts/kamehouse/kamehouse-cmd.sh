#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=true
TAIL_LOG_AWK=${HOME}/my.scripts/awk/kamehouse/format-tail-log.awk

mainProcess() {
  # Execute the latest deployed version of kamehouse-cmd
  ${HOME}/programs/kamehouse-cmd/bin/kamehouse-cmd.sh "$@" | ${TAIL_LOG_AWK}
}

parseArguments() {
  # Override default. Skip parsing as it's done in kamehouse-cmd
  return
}

main "$@"
