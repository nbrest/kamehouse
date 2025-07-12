#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/desktop/desktop-functions.sh
if [ "$?" != "0" ]; then echo "Error importing desktop-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

mainProcess() {
  setupLinuxEnvironment
  setKameHouseDesktopPid
  if [ -n "${KAMEHOUSE_DESKTOP_PID}" ]; then
    log.warn "kamehouse-desktop is already running. Exiting..."
    exitSuccessfully
  fi
  startKameHouseDesktop
}

startKameHouseDesktop() {
  log.info "Starting ${COL_PURPLE}kamehouse-desktop${COL_DEFAULT_LOG}"
  cd ${HOME}/programs/kamehouse-desktop
  export OPENWEATHERMAP_API_KEY=${OPENWEATHERMAP_API_KEY}
  python ${HOME}/programs/kamehouse-desktop/bin/kamehouse_desktop.py &
}

main "$@"
