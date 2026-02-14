#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/keep-alive/keep-alive-functions.sh

initScriptEnv() {
  PORT=9090
  KEEP_ALIVE_SERVICE="tomcat"
  KEEP_ALIVE_SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/tomcat/tomcat-restart.sh"
}

setKeepAliveServicePidLin() {
  netstat -nltp | grep ":${PORT} " | grep java 
  KEEP_ALIVE_SERVICE_PID=`netstat -nltp | grep ":${PORT} " | grep java | awk '{print $7}' | cut -d '/' -f 1`
}

setKeepAliveServicePidWin() {
  netstat -ano | grep "LISTENING" | grep ":${PORT} " | tail -n 1
  KEEP_ALIVE_SERVICE_PID=`netstat -ano | grep "LISTENING" | grep ":${PORT} " | tail -n 1 | awk '{print $5}'`
}

main "$@"
