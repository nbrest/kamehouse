#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/kamehouse/desktop/desktop-functions.sh

mainProcess() {
  setKameHouseDesktopPid
  if [ -z "${KAMEHOUSE_DESKTOP_PID}" ]; then
    log.info "kamehouse-desktop is not running"
    exitSuccessfully
  fi

  if ${IS_LINUX_HOST}; then
    log.info "Killing process ${COL_PURPLE}${KAMEHOUSE_DESKTOP_PID}"
    kill -9 ${KAMEHOUSE_DESKTOP_PID}
  else
    log.info "Killing process ${COL_PURPLE}${KAMEHOUSE_DESKTOP_PID}"
    powershell.exe -c "taskkill.exe /PID ${KAMEHOUSE_DESKTOP_PID} /F"
    powershell.exe -c "Stop-Process -Id ${KAMEHOUSE_DESKTOP_PID} -Force"
  fi
}

main "$@"
