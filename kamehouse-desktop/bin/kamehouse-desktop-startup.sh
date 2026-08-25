#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/desktop/desktop-functions.sh

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptConfig() {
  # Set the screen to use to render kamehouse-desktop
  KAMEHOUSE_DESKTOP_SCREEN=""
}

mainProcess() {
  setupLinuxEnvironment
  setKameHouseDesktopPid
  if [ -n "${KAMEHOUSE_DESKTOP_PID}" ]; then
    log.warn "kamehouse-desktop is already running. Exiting..."
    exitSuccessfully
  fi
  initDesktopBackgroundsLists
  exportAppVariables
  startKameHouseDesktop
}

exportAppVariables() {
  export OPENWEATHERMAP_API_KEY=${OPENWEATHERMAP_API_KEY}

  if [ -n "${KAMEHOUSE_DESKTOP_SCREEN}" ]; then
    log.info "Exporting KAMEHOUSE_DESKTOP_SCREEN=${KAMEHOUSE_DESKTOP_SCREEN}"
    export KAMEHOUSE_DESKTOP_SCREEN=${KAMEHOUSE_DESKTOP_SCREEN}
  fi
}

startKameHouseDesktop() {
  log.info "Starting ${COL_PURPLE}kamehouse-desktop${COL_DEFAULT_LOG} version:"
  echo -ne "${COL_YELLOW_STD}     "
  ${HOME}/programs/kamehouse-shell/bin/desktop/kamehouse-desktop-version.sh
  echo -ne "${COL_NORMAL}"
  cd ${HOME}/programs/kamehouse-desktop
  python ${HOME}/programs/kamehouse-desktop/bin/kamehouse_desktop.py &
}

main "$@"
