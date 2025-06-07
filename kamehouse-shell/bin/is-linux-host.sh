#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
  LOG_PROCESS_TO_FILE=false
}

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
