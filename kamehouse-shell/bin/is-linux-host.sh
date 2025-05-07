#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 99
fi

LOG=DISABLED
LOG_PROCESS_TO_FILE=false

# Returns true if it's a linux host, false if it isn't.
# It can also be infered by the return value. 
# Returns 0 for true, 1 for false.
mainProcess() {
	echo ${IS_LINUX_HOST}
  if ${IS_LINUX_HOST}; then
    exit ${EXIT_SUCCESS}
  else
    exit ${EXIT_ERROR}
  fi
}

main "$@"
