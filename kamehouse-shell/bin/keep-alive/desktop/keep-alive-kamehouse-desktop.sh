#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/keep-alive/keep-alive-functions.sh
importKamehouse functions/desktop/desktop-functions.sh

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
}

initScriptEnv() {
  KEEP_ALIVE_SERVICE="kamehouse-desktop"
  KEEP_ALIVE_SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/desktop/kamehouse-desktop-startup.sh"
}

setKeepAliveServicePidLin() {
  setKameHouseDesktopPid
  KEEP_ALIVE_SERVICE_PID=${KAMEHOUSE_DESKTOP_PID}
}

setKeepAliveServicePidWin() {
  setKameHouseDesktopPid
  KEEP_ALIVE_SERVICE_PID=${KAMEHOUSE_DESKTOP_PID}
}

main "$@"
