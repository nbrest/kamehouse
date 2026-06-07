#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  if ${IS_LINUX_HOST}; then
    processLin
  else
    processWin
  fi
}

processLin() {
  setupLinuxEnvironment
  gnome-screensaver-command -l
}

processWin() {
  log.info "Locking screen"
  rundll32.exe user32.dll,LockWorkStation
}

main "$@"
