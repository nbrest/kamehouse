#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  VLC_HTTP_PORT="8080"
}

mainProcess() {
  log.info "Searching for apache httpd process"
  netstat -ano | grep "LISTENING" | grep "\[::\]:${HTTPD_PORT} " | grep -v ${VLC_HTTP_PORT} | tail -n 1
  HTTPD_PID=`netstat -ano | grep "LISTENING" | grep "\[::\]:${HTTPD_PORT} " | grep -v ${VLC_HTTP_PORT} | tail -n 1 | awk '{print $5}' | cut -d '/' -f 1`
  if [ -z "${HTTPD_PID}" ]; then
    log.info "Apache httpd is not running"
  else
    log.info "Killing process ${COL_PURPLE}${HTTPD_PID}"
    cmd.exe "/c taskkill.exe /PID ${HTTPD_PID} /F"
    powershell.exe -c "Stop-Process -Id ${HTTPD_PID} -Force"
  fi
}

parseArguments() {
  parseHttpdPort "$@"
}

setEnvFromArguments() {
  setEnvForHttpdPort
}

printHelpOptions() {
  printHttpdPortOption
}

main "$@"
