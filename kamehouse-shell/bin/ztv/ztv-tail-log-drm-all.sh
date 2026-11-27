#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/ztv/ztv-functions.sh

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
}

initScriptEnv() {
  ALLOW_PARALLEL_RUNS=true
  FOLLOW="-F"
}

mainProcess() {
  checkExistingZtvLogFile
  LAST_M3U_LINE=`grep -n -e "main debug:  (path:.*.m3u" ${ZTV_LOG_FILE} | cut -d ':' -f 1 | tail -n 1`
  TAIL_GREP_REGEX="drm_vout.*|main debug:  \(path:..*(${ZTV_STATS_MEDIA_FILES}).*"
  tail -n +${LAST_M3U_LINE} ${FOLLOW} ${ZTV_LOG_FILE} | grep -E "${TAIL_GREP_REGEX}"
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
