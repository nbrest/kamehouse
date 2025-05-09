#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
	echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99
fi

mainProcess() {
  netstat -ano | grep "LISTENING" | grep "\[::\]:${VLC_PORT} " | tail -n 1
  cmd.exe "/c tasklist.exe /FI IMAGENAME eq vlc.exe"
}

main "$@"
