#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcessLin() {
  setSudoKameHouseCommand "/usr/bin/systemctl"
  ${SUDO_KAMEHOUSE_COMMAND} suspend -i
}

mainProcessWin() {
  powershell.exe -c "rundll32.exe powrprof.dll,SetSuspendState 0,1,0"
}

main "$@"
