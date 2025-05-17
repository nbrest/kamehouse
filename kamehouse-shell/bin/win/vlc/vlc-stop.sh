#!/bin/bash

# Fix websockets reconnecting constantly

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/vlc/vlc-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing vlc-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  DEFAULT_VLC_PORT="8080"
  VLC_PORT=""
}

mainProcess() {
  log.debug "VLC_PORT ${VLC_PORT}"
  log.info "Searching for vlc process with an http server"
  netstat -ano | grep "LISTENING" | grep "\[::\]:${VLC_PORT} " | tail -n 1
  VLC_PID=`netstat -ano | grep "LISTENING" | grep "\[::\]:${VLC_PORT} " | tail -n 1 | awk '{print $5}' | cut -d '/' -f 1`
  log.info "VLC_PID: ${VLC_PID}" 
  if [ -z "${VLC_PID}" ]; then
    log.info "vlc is not running with an http server"
  else
    log.info "Killing process ${COL_PURPLE}${VLC_PID}"
    cmd.exe "/c taskkill.exe /PID ${VLC_PID} /F"
  fi
  log.info "Killing remaining vlc process"
  cmd.exe "/c taskkill /im vlc.exe"
  removeVlcProcessInfo
  rotateVlcLog
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
        VLC_PORT="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  if [ -z "${VLC_PORT}" ]; then
    VLC_PORT=${DEFAULT_VLC_PORT}
  fi  
}

printHelpOptions() {
  addHelpOption "-p" "vlc port. Default ${DEFAULT_VLC_PORT}"
}

main "$@"
