#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi
importFunctions ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/desktop/desktop-functions.sh

mainProcess() {
  setKameHouseDesktopPid
  if [ -z "${KAMEHOUSE_DESKTOP_PID}" ]; then
    log.info "kamehouse-desktop is not running"
  else
    log.info "kamehouse-desktop is running with pid ${COL_PURPLE}${KAMEHOUSE_DESKTOP_PID}"
  fi
}

main "$@"
