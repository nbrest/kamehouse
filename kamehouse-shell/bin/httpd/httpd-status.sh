#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  if ${IS_LINUX_HOST}; then
    processLin
  else
    processWin
  fi
}

processLin() {
  log.info "Searching for apache httpd process"
  setSudoKameHouseCommand "netstat -nltp"
  HTTPD_PID=`${SUDO_KAMEHOUSE_COMMAND} | grep ":${HTTPD_PORT} " | grep apache | awk '{print $7}' | cut -d '/' -f 1`
  if [ -z "${HTTPD_PID}" ]; then
    log.info "Apache httpd is not running"
  else
    log.info "Apache httpd is currently running with pid ${COL_PURPLE}${HTTPD_PID}${COL_DEFAULT_LOG} on port ${COL_PURPLE}${HTTPD_PORT}"
  fi
}

processWin() {
  log.info "Searching for apache httpd process"
  netstat -ano | grep "LISTENING" | grep "\[::\]:${HTTPD_PORT} " | tail -n 1
  HTTPD_PID=`netstat -ano | grep "LISTENING" | grep "\[::\]:${HTTPD_PORT} " | tail -n 1 | awk '{print $5}' | cut -d '/' -f 1`
  if [ -z "${HTTPD_PID}" ]; then
    log.info "Apache httpd is not running"
  else
    log.info "Apache httpd is currently running with pid ${COL_PURPLE}${HTTPD_PID}${COL_DEFAULT_LOG} on port ${COL_PURPLE}${HTTPD_PORT}"
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
