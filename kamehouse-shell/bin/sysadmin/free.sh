#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

mainProcess() {
  if ${IS_LINUX_HOST}; then
    processLin
  else
    processWin
  fi
}

processLin() {
  free -h
}

processWin() {
  log.info "Executing 'powershell.exe -c \"systeminfo\" | grep Memory'"
  powershell.exe -c "systeminfo" | grep Memory
}

main "$@"

