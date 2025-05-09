#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/vlc/vlc-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing vlc-functions.sh" ; exit 99
fi

initScriptEnv() {
  VLC_STATS_ARGS=""
}

mainProcess() {
  checkRunningVlcProcess
  showCurrentRunPlayedFilesSortedHeadTail
  showVlcLogsFiltered
  showCurrentRunPlayedFilesHeadTail
  showVlcSystemProcessInfo
  showVlcStats
  showVlcLogsLastDrm
}

showCurrentRunPlayedFilesSortedHeadTail() {
  local PLAYLIST=`${HOME}/programs/kamehouse-shell/bin/common/vlc/vlc-get-current-run-played-files.sh --sort-alpha`
  echo "${PLAYLIST}" | head -n 10
  echo "..."
  echo "${PLAYLIST}" | tail -n 10  
}

showVlcLogsFiltered() {
  checkExistingVlcLogFile
  log.info "tail filtered vlc logs"
  local LAST_M3U_LINE=`grep -n -e "main debug:  (path:.*.m3u" ${VLC_LOG_FILE} | cut -d ':' -f 1 | tail -n 1`
  local TAIL_GREP_REGEX="drm_vout.*|main debug:  \(path:..*(${VLC_STATS_MEDIA_FILES}).*"
  echo -ne "${COL_RED}"
  tail -n +${LAST_M3U_LINE} ${VLC_LOG_FILE} | grep -E "${TAIL_GREP_REGEX}" | tail -n 100
  echo -ne "${COL_NORMAL}"
}

showCurrentRunPlayedFilesHeadTail() {
  local PLAYLIST=`${HOME}/programs/kamehouse-shell/bin/common/vlc/vlc-get-current-run-played-files.sh`
  echo "${PLAYLIST}" | head -n 10
  echo "..."
  echo "${PLAYLIST}" | tail -n 10
}

showVlcSystemProcessInfo() {
  if ! ${IS_LINUX_HOST}; then
    return
  fi
  log.info "Running top command to check vlc process status"
  local VLC_PID=`ps -ef | grep vlc | grep -v "vlc-start.sh" | grep -E ".*(${VLC_STATS_MEDIA_FILES}).*" | awk '{print $2}'`
  local TOP_OUTPUT=`top -e m -E m -p "${VLC_PID}" -n 1 -b`
  echo "${TOP_OUTPUT}"

  log.info "ps -ef | grep vlc | grep -v vlc-start.sh"
  ps -ef | grep vlc | grep -v "vlc-start.sh" | grep -E ".*(${VLC_STATS_MEDIA_FILES}).*"
}  

showVlcStats() {
  if ${IS_LINUX_HOST}; then
    VLC_STATS_ARGS="${VLC_STATS_ARGS} -n 8"
  else
    VLC_STATS_ARGS="${VLC_STATS_ARGS} -n 2"
  fi
  ${HOME}/programs/kamehouse-shell/bin/common/vlc/vlc-stats.sh ${VLC_STATS_ARGS}
}

showVlcLogsLastDrm() {
  checkExistingVlcLogFile
  local LAST_M3U_LINE=`grep -n -e "main debug:  (path:.*.m3u" ${VLC_LOG_FILE} | cut -d ':' -f 1 | tail -n 1`
  local TAIL_GREP_REGEX="drm_vout debug: OK simple pic test.*|drm_vout debug: get_lease_fd OK.*|drm_vout error: Failed to get xlease.*|main debug:  \(path:..*(${VLC_STATS_MEDIA_FILES}).*"
  log.info "Last drm filtered entries of vlc.log"
  tail -n +${LAST_M3U_LINE} ${VLC_LOG_FILE} | grep -E "${TAIL_GREP_REGEX}" | tail -n 10
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
      -t|--show-history-file-only)
        VLC_STATS_ARGS="${VLC_STATS_ARGS} --show-history-file-only"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-t --show-history-file-only" "trace. just show the existing stats file without any calculations"
}

main "$@"
