#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  ALLOW_PARALLEL_RUNS=true
}

mainProcessLin() {
  psListProcesses | grep -e "vlc\|COMMAND" | grep -v grep
}

mainProcessWin() {
  netstat -ano | grep "LISTENING" | grep "\[::\]:${ZTV_PORT} " | tail -n 1
  powershell.exe -c "tasklist.exe /FI IMAGENAME eq vlc.exe"
}

main "$@"
