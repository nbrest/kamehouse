#!/bin/bash

# Fix websockets reconnecting constantly

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

importKamehouse functions/ztv/ztv-functions.sh

initScriptEnv() {
  DEFAULT_ZTV_PORT="8080"
  ZTV_PORT=""
}

mainProcessLin() {
  log.debug "ZTV_PORT ${ZTV_PORT}"
  log.info "Searching for ZTV process with an http server"
  netstat -nltp | grep ":${ZTV_PORT} " | grep vlc | grep -v tcp6 | awk '{print $7}' | cut -d '/' -f 1
  ZTV_PID=`netstat -nltp | grep ":${ZTV_PORT} " | grep vlc | grep -v tcp6 | awk '{print $7}' | cut -d '/' -f 1`
  log.info "ZTV_PID: ${ZTV_PID}"
  if [ -z "${ZTV_PID}" ]; then
    log.info "ZTV Player is not running with an http server"
  else
    log.info "Killing process ${COL_PURPLE}${ZTV_PID}"
    kill -9 ${ZTV_PID}
  fi
  log.info "Killing remaining ZTV process"
  KILL_ZTV_PID=`psListProcesses | grep vlc | grep -v grep | grep -v ZtvProcessController | grep -v surefire | grep -v failsafe\\:integration-test | grep -v build-kamehouse\\.sh | awk -v pidField="${PS_PID_FIELD}" '{print $pidField}'`
  if [ -n "${KILL_ZTV_PID}" ]; then
    kill -9 ${KILL_ZTV_PID}
  fi
  removeZtvProcessInfo
  rotateZtvLog
}

mainProcessWin() {
  log.debug "ZTV_PORT ${ZTV_PORT}"
  log.info "Searching for ZTV process with an http server"
  netstat -ano | grep "LISTENING" | grep "\[::\]:${ZTV_PORT} " | tail -n 1
  ZTV_PID=`netstat -ano | grep "LISTENING" | grep "\[::\]:${ZTV_PORT} " | tail -n 1 | awk '{print $5}' | cut -d '/' -f 1`
  log.info "ZTV_PID: ${ZTV_PID}"
  if [ -z "${ZTV_PID}" ]; then
    log.info "ZTV Player is not running with an http server"
  else
    log.info "Killing process ${COL_PURPLE}${ZTV_PID}"
    powershell.exe -c "taskkill.exe /PID ${ZTV_PID} /F"
    powershell.exe -c "Stop-Process -Id ${ZTV_PID} -Force"
  fi
  log.info "Killing remaining ZTV process"
  powershell.exe -c "taskkill /im vlc.exe"
  powershell.exe -c "Stop-Process -Name vlc.exe -Force"
  removeZtvProcessInfo
  rotateZtvLog
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -p)
        ZTV_PORT="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  if [ -z "${ZTV_PORT}" ]; then
    ZTV_PORT=${DEFAULT_ZTV_PORT}
  fi  
}

printHelpOptions() {
  addHelpOption "-p" "ztv port. Default ${DEFAULT_ZTV_PORT}"
}

main "$@"
