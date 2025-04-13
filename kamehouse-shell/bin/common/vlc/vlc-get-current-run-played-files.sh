#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/vlc/vlc-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing vlc-functions.sh\033[0;39m"
  exit 99
fi

SORT_ALPHABETICALLY=false
FILES_PLAYED=""
QUIET_MODE=false
COL_FILES_PLAYED="${COL_CYAN}"

mainProcess() {
  checkRunningVlcProcess
  getFilesPlayed
  showFilesPlayed
  showFilesPlayedCount
}

getFilesPlayed() {
  checkExistingVlcLogFile
  log.debug "Getting all files played during the current vlc playlist run"
  local LAST_M3U_LINE=`grep -n -e "main debug:  (path:.*.m3u" ${VLC_LOG_FILE} | cut -d ':' -f 1 | tail -n 1`
  if [ -z "${LAST_M3U_LINE}" ]; then
    LAST_M3U_LINE=0
  fi
  FILES_PLAYED=`tail -n +${LAST_M3U_LINE} ${VLC_LOG_FILE} | grep -e "main debug:  (path: " | grep -E ".*(${VLC_STATS_MEDIA_FILES}).*" | awk '{print $4}' | sed 's/.$//' | sed 's#\\\#/#Ig'`
 
  echo -e "${FILES_PLAYED}" > "${VLC_CURRENT_RUN_PLAYED_FILES}"  
}

showFilesPlayed() {
  if ${QUIET_MODE}; then
    log.debug "Running in quiet mode, skip printing file contents"
    return
  fi
  if ${SORT_ALPHABETICALLY}; then
    log.info "Sorting files alphabetically"
    FILES_PLAYED=`echo -e "${FILES_PLAYED}" | sort`
    COL_FILES_PLAYED="${COL_YELLOW}"
  fi
  echo -ne "${COL_FILES_PLAYED}"
  echo -e "${FILES_PLAYED}"
  echo -ne "${COL_NORMAL}"
}

showFilesPlayedCount() {
  if ${QUIET_MODE}; then
    return
  fi
  local let FILES_PLAYED_COUNT=`echo -e "${FILES_PLAYED}" | wc -l`
  FILES_PLAYED_COUNT=$((FILES_PLAYED_COUNT - 1))
  log.info "Number of files played: ${COL_PURPLE}${FILES_PLAYED_COUNT}"
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
      -a|--sort-alpha)
        SORT_ALPHABETICALLY=true
        ;;
      -q|--quiet-mode)
        QUIET_MODE=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-a --sort-alpha" "sort alphabetically rather than playing order"
  addHelpOption "-q --quiet-mode" "just update list files without printing the content to the console output"
}

main "$@"
