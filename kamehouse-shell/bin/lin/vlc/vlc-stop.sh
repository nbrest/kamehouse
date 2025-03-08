#!/bin/bash

# Fix websockets reconnecting constantly

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/vlc/vlc-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing vlc-functions.sh\033[0;39m"
  exit 99
fi

DEFAULT_VLC_PORT="8080"
VLC_PORT=""

mainProcess() {
  log.debug "VLC_PORT ${VLC_PORT}"
  log.info "Searching for vlc process with an http server"
  netstat -nltp | grep ":${VLC_PORT} " | grep vlc | grep -v tcp6 | awk '{print $7}' | cut -d '/' -f 1
  VLC_PID=`netstat -nltp | grep ":${VLC_PORT} " | grep vlc | grep -v tcp6 | awk '{print $7}' | cut -d '/' -f 1`
  log.info "VLC_PID: ${VLC_PID}"
  if [ -z "${VLC_PID}" ]; then
    log.info "vlc is not running with an http server"
  else
    log.info "Killing process ${COL_PURPLE}${VLC_PID}"
    kill -9 ${VLC_PID}
  fi
  log.info "Killing remaining vlc process"
  KILL_VLC_PID=`ps aux | grep vlc | grep -v grep | grep -v VlcProcessController | grep -v surefire | grep -v failsafe\\:integration-test | grep -v build-kamehouse\\.sh | awk '{print $2}'`
  if [ -z "${KILL_VLC_PID}" ]; then
    kill -9 ${KILL_VLC_PID}
  fi
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
