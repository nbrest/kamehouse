#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

mainProcessLin() {
  TERM=xterm top -b -n 1
}

mainProcessWin() {
  log.info "Executing 'powershell.exe -c \"ps | sort -desc cpu | select -first 20\"'"
  powershell.exe -c "ps | sort -desc cpu | select -first 20"
}

main "$@"
