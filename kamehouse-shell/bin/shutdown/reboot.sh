#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcessLin() {
  setSudoKameHouseCommand "/usr/sbin/reboot"
  ${SUDO_KAMEHOUSE_COMMAND}

  setSudoKameHouseCommand "/sbin/reboot"
  ${SUDO_KAMEHOUSE_COMMAND}
}

mainProcessWin() {
  powershell.exe -c "shutdown.exe /r /f /t 0"
}

main "$@"
