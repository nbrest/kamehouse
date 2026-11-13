#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/groot/groot-functions.sh
importKamehouse functions/keep-alive/keep-alive-functions.sh

initScriptEnv() {
  KEEP_ALIVE_SERVICE="groot"
  KEEP_ALIVE_SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/groot/kamehouse-groot-restart.sh"
}

mainProcessLin() {
  setKameHouseGrootPid
  KEEP_ALIVE_SERVICE_PID=${KAMEHOUSE_GROOT_PID}
}

mainProcessWin() {
  setKameHouseGrootPid
  KEEP_ALIVE_SERVICE_PID=${KAMEHOUSE_GROOT_PID}
}

main "$@"
