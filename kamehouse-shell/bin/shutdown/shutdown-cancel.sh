#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  if ${IS_LINUX_HOST}; then
    processLin
  else
    processWin
  fi
}

processLin() {
  setSudoKameHouseCommand "/usr/sbin/shutdown"
  ${SUDO_KAMEHOUSE_COMMAND} -c
}

processWin() {
  powershell.exe -c "shutdown.exe /a"
}

main "$@"
