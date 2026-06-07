#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
  LOG_PROCESS_TO_FILE=false
}

# Returns true if it's a linux host, false if it isn't.
# It can also be infered by the return value. 
# Returns 0 for true, 1 for false.

mainProcessPre() {
	echo ${IS_LINUX_HOST}
}

mainProcessLin() {
  exit ${EXIT_SUCCESS}
}

mainProcessWin() {
  exit ${EXIT_ERROR}
}

main "$@"
