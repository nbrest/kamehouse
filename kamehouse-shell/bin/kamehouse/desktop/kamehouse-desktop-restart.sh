#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
  LOG_PROCESS_TO_FILE=false
}

mainProcess() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/desktop/kamehouse-desktop-stop.sh
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/desktop/kamehouse-desktop-startup.sh
}

main "$@"
