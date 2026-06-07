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
  ps aux | grep -e "vlc\|COMMAND" | grep -v grep
}

processWin() {
  netstat -ano | grep "LISTENING" | grep "\[::\]:${VLC_PORT} " | tail -n 1
  powershell.exe -c "tasklist.exe /FI IMAGENAME eq vlc.exe"
}

main "$@"
