#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcessLin() {
  setupLinuxEnvironment
  gnome-screensaver-command -l
}

mainProcessWin() {
  log.info "Locking screen"
  rundll32.exe user32.dll,LockWorkStation
}

main "$@"
