#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/ztv/ztv-functions.sh

initScriptEnv() {
  ZTV_STATS_ARGS=""
}

mainProcess() {
  checkRunningZtvProcess
  showCurrentRunPlayedFilesSortedHeadTail
  showZtvLogsFiltered
  showCurrentRunPlayedFilesHeadTail
  showZtvSystemProcessInfo
  showZtvStats
  showZtvLogsLastDrm
}

showCurrentRunPlayedFilesSortedHeadTail() {
  local PLAYLIST=`${HOME}/.kamehouse/opt/kamehouse-shell/bin/ztv/ztv-get-current-run-played-files.sh --sort-alpha`
  echo "${PLAYLIST}" | head -n 10
  echo "..."
  echo "${PLAYLIST}" | tail -n 10  
}

showZtvLogsFiltered() {
  checkExistingZtvLogFile
  log.info "tail filtered ztv logs"
  local LAST_M3U_LINE=`grep -n -e "main debug:  (path:.*.m3u" ${ZTV_LOG_FILE} | cut -d ':' -f 1 | tail -n 1`
  local TAIL_GREP_REGEX="drm_vout.*|main debug:  \(path:..*(${ZTV_STATS_MEDIA_FILES}).*"
  echo -ne "${COL_RED}"
  tail -n +${LAST_M3U_LINE} ${ZTV_LOG_FILE} | grep -E "${TAIL_GREP_REGEX}" | tail -n 100
  echo -ne "${COL_NORMAL}"
}

showCurrentRunPlayedFilesHeadTail() {
  local PLAYLIST=`${HOME}/.kamehouse/opt/kamehouse-shell/bin/ztv/ztv-get-current-run-played-files.sh`
  echo "${PLAYLIST}" | head -n 10
  echo "..."
  echo "${PLAYLIST}" | tail -n 10
}

showZtvSystemProcessInfo() {
  if ! ${IS_LIN_HOST}; then
    return
  fi
  log.info "Running top command to check ZTV process status"
  local ZTV_PID=`psListProcesses | grep vlc | grep -v "ztv-start.sh" | grep -E ".*(${ZTV_STATS_MEDIA_FILES}).*" | awk -v pidField="${PS_PID_FIELD}" '{print $pidField}'`
  local TOP_OUTPUT=`top -e m -E m -p "${ZTV_PID}" -n 1 -b`
  echo "${TOP_OUTPUT}"

  log.info "psListProcesses | grep vlc | grep -v ztv-start.sh"
  psListProcesses | grep vlc | grep -v "ztv-start.sh" | grep -E ".*(${ZTV_STATS_MEDIA_FILES}).*"
}  

showZtvStats() {
  if ${IS_LIN_HOST}; then
    ZTV_STATS_ARGS="${ZTV_STATS_ARGS} -n 8"
  else
    ZTV_STATS_ARGS="${ZTV_STATS_ARGS} -n 2"
  fi
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/ztv/ztv-stats.sh ${ZTV_STATS_ARGS}
}

showZtvLogsLastDrm() {
  checkExistingZtvLogFile
  local LAST_M3U_LINE=`grep -n -e "main debug:  (path:.*.m3u" ${ZTV_LOG_FILE} | cut -d ':' -f 1 | tail -n 1`
  local TAIL_GREP_REGEX="drm_vout debug: OK simple pic test.*|drm_vout debug: get_lease_fd OK.*|drm_vout error: Failed to get xlease.*|main debug:  \(path:..*(${ZTV_STATS_MEDIA_FILES}).*"
  log.info "Last drm filtered entries of ztv.log"
  tail -n +${LAST_M3U_LINE} ${ZTV_LOG_FILE} | grep -E "${TAIL_GREP_REGEX}" | tail -n 10
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
        ZTV_STATS_ARGS="${ZTV_STATS_ARGS} --show-history-file-only"
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
