#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

mainProcessLin() {
  free -h
}

mainProcessWin() {
  log.info "Executing 'powershell.exe -c \"systeminfo\" | grep Memory'"
  powershell.exe -c "systeminfo" | grep Memory
}

main "$@"

