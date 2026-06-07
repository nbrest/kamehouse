#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

mainProcessLin() {
  sudo df -h
}

mainProcessWin() {
  log.info "Executing 'powershell.exe -c \"gdr\"'"
  powershell.exe -c "gdr"
}

main "$@"
