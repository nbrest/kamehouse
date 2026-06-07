#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  if ${IS_LINUX_HOST}; then
    processLin
  else
    processWin
  fi
}

processLin() {
  ps aux | grep -e "shutdown\|COMMAND" | grep -v grep
}

processWin() {
  powershell.exe -c "tasklist.exe /FI IMAGENAME eq shutdown.exe"
}

main "$@"
