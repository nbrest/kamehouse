#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/kamehouse/desktop/desktop-functions.sh

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
}

initScriptEnv() {
  SERVICE="kamehouse-desktop"
  SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/desktop/kamehouse-desktop-startup.sh"
}

mainProcess() {
  checkKeepAliveScriptsEnabled
  setKameHouseDesktopPid
  if [ -z "${KAMEHOUSE_DESKTOP_PID}" ]; then
    log.error "${SERVICE} not running. Starting it now"
    ${SERVICE_STARTUP} &
  else
    log.info "${SERVICE} is currently running with pid ${COL_PURPLE}${KAMEHOUSE_DESKTOP_PID}"
  fi
}

main "$@"
