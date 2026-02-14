#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/keep-alive/keep-alive-functions.sh

initScriptEnv() {
  PORT=443
  KEEP_ALIVE_SERVICE="httpd"
  KEEP_ALIVE_SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/httpd/httpd-restart.sh"
}

setKeepAliveServicePidLin() {
  logNeedsSudoPermissions netstat
  sudo netstat -nltp | grep ":${PORT} "
  KEEP_ALIVE_SERVICE_PID=`sudo netstat -nltp | grep ":${PORT} " | awk '{print $7}' | cut -d '/' -f 1`
}

setKeepAliveServicePidWin() {
  netstat -ano | grep "LISTENING" | grep ":${PORT} " | tail -n 1
  KEEP_ALIVE_SERVICE_PID=`netstat -ano | grep "LISTENING" | grep ":${PORT} " | tail -n 1 | awk '{print $5}'`
}

main "$@"
