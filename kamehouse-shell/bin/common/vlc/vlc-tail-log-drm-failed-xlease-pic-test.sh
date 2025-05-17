#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/vlc/vlc-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing vlc-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
}

initScriptEnv() {
  FOLLOW="-F"
}

mainProcess() {
  checkExistingVlcLogFile
  LAST_M3U_LINE=`grep -n -e "main debug:  (path:.*.m3u" ${VLC_LOG_FILE} | cut -d ':' -f 1 | tail -n 1`
  TAIL_GREP_REGEX="drm_vout debug: OK simple pic test.*|drm_vout debug: get_lease_fd OK.*|drm_vout error: Failed to get xlease.*|main debug:  \(path:..*(${VLC_STATS_MEDIA_FILES}).*"
  tail -n +${LAST_M3U_LINE} ${FOLLOW} ${VLC_LOG_FILE} | grep -E "${TAIL_GREP_REGEX}"
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
      -q)
        FOLLOW=""
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-q" "quit after tailing once. Don't follow log"
}

main "$@"
