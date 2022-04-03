#!/bin/bash

# Fix websockets reconnecting constantly

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

LOG_PROCESS_TO_FILE=true
DEFAULT_VLC_PORT="8080"
VLC_PORT=""

mainProcess() {
  VLC_PORT_PARAM=$1
  if [ -z ${VLC_PORT_PARAM} ]; then
    VLC_PORT=${DEFAULT_VLC_PORT}
  else
    VLC_PORT=${VLC_PORT_PARAM}
  fi
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
  log.info "Killing remaining vlc.exe process"
  cmd.exe "/c taskkill /im vlc.exe"
}

main "$@"
