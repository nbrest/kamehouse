#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
  LOG_PROCESS_TO_FILE=false
}

mainProcess() {
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/desktop/kamehouse-desktop-stop.sh
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/desktop/kamehouse-desktop-startup.sh &
}

main "$@"
