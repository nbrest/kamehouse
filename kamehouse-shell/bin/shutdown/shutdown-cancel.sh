#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcessLin() {
  setSudoKameHouseCommand "/usr/sbin/shutdown"
  ${SUDO_KAMEHOUSE_COMMAND} -c
}

mainProcessWin() {
  powershell.exe -c "shutdown.exe /a"
}

main "$@"
