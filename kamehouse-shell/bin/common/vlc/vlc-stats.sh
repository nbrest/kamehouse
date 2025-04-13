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

NUM_STAT_ITERATIONS_TO_KEEP=10000
NUMBER_OF_STAT_ITERATIONS=0
SHOW_CURRENT_STATS_ONLY=false
SHOW_HISTORY_FILE_ONLY=false

let TOTAL_MINS_PLAYED=0
let NUM_FILES_PLAYED=0
let HH_PLAYED=0
let MM_PLAYED=0

mainProcess() {
  checkRunningVlcProcess
  initStatsHistoryFile
  if ${SHOW_HISTORY_FILE_ONLY}; then
    showStatsHistoryFile
    exitSuccessfully
  fi
  calculateCurrentRunStats
  if ${SHOW_CURRENT_STATS_ONLY}; then
    showCurrentRunStats
    exitSuccessfully
  fi  
  updateStatsHistoryFile
  showStatsHistoryFile
}

initStatsHistoryFile() {
  if [ ! -f "${VLC_STATS_HISTORY_FILE}" ]; then
    log.info "Initializing file ${VLC_STATS_HISTORY_FILE}"
    echo -ne "" > "${VLC_STATS_HISTORY_FILE}"
  fi
}

calculateCurrentRunStats() {
  if [ ! -f "${VLC_PROCESS_INFO_FILE}" ]; then
    log.error "${VLC_PROCESS_INFO_FILE} file not found. Can't calculate current run stats. Restart vlc through kamehouse"
    exitProcess ${EXIT_ERROR}
  fi  
  source "${VLC_PROCESS_INFO_FILE}"
  local CURRENT_DATE_SECONDS="$(date +%s)"
  local VLC_PROCESS_START_DATE_SECONDS="$(date -d "${VLC_PROCESS_START_DATE}" +%s)"
  TOTAL_MINS_PLAYED=$((CURRENT_DATE_SECONDS - VLC_PROCESS_START_DATE_SECONDS))
  TOTAL_MINS_PLAYED=$((TOTAL_MINS_PLAYED / 60)) 
  log.trace "TOTAL_MINS_PLAYED=${TOTAL_MINS_PLAYED}"
  HH_PLAYED=$((TOTAL_MINS_PLAYED / 60)) 
  MM_PLAYED=$((HH_PLAYED * 60)) 
  if [ "${MM_PLAYED}" != "0" ]; then
    MM_PLAYED=${MM_PLAYED#0}
  fi
  MM_PLAYED=$((TOTAL_MINS_PLAYED - MM_PLAYED)) 

  ${HOME}/programs/kamehouse-shell/bin/common/vlc/vlc-get-current-run-played-files.sh --quiet-mode
  LAST_PLAYED_FILE=`tail -n 1 "${VLC_CURRENT_RUN_PLAYED_FILES}" | sed 's#\\\#/#Ig'`
  NUM_FILES_PLAYED=`cat "${VLC_CURRENT_RUN_PLAYED_FILES}" | wc -l`
  NUM_FILES_PLAYED=$((NUM_FILES_PLAYED - 1)) # remove .m3u from count
}

showCurrentRunStats() {
  log.info "${COL_RED}**************************************************"
  log.info "${COL_YELLOW}VLC run stats:"
  log.info "VLC load file: ${COL_PURPLE}${VLC_CURRENT_FILE_LOADED}"
  log.info "Current file: ${COL_PURPLE}${LAST_PLAYED_FILE}"
  log.info "VLC run start: ${COL_RED}${VLC_PROCESS_START_DATE}" 
  log.info "VLC run time:  ${COL_RED}${HH_PLAYED} hs ${MM_PLAYED} mins"
  log.info "Files played count:  ${COL_RED}${NUM_FILES_PLAYED}"
  logTemperature
  logVlcCpuUsage
}

logVlcCpuUsage() {
  if ! ${IS_LINUX_HOST}; then
    return
  fi
  local VLC_PID=`ps -ef | grep vlc | grep -v "vlc-start.sh" | grep -E ".*(\.mp3|\.MP3|\.mp4|\.MP4|\.mkv|\.MKV|\.m3u|\.M3U).*" | awk '{print $2}'`
  local VLC_CPU_USAGE=`top -e m -E m -p "${VLC_PID}" -n 1 -b | grep vlc | awk '{print $9}'`
  log.info "VLC cpu usage: ${COL_RED}${VLC_CPU_USAGE}%"
}

logTemperature() {
  if ! ${IS_LINUX_HOST}; then
    return
  fi
  local TEMPERATURE=`vcgencmd measure_temp | grep "temp="`
  log.info "Temperature: ${COL_RED}${TEMPERATURE}"
}

updateStatsHistoryFile() {
  trimStatsHistoryFile
  log.info "${COL_RED}**************************************************" >> "${VLC_STATS_HISTORY_FILE}"
  log.info "${COL_YELLOW}VLC run stats:" >> "${VLC_STATS_HISTORY_FILE}"
  log.info "VLC load file: ${COL_PURPLE}${VLC_CURRENT_FILE_LOADED}" >> "${VLC_STATS_HISTORY_FILE}"
  log.info "Current file:  ${COL_PURPLE}${LAST_PLAYED_FILE}" >> "${VLC_STATS_HISTORY_FILE}"
  log.info "VLC run start: ${COL_RED}${VLC_PROCESS_START_DATE}" >> "${VLC_STATS_HISTORY_FILE}"
  log.info "VLC run time:  ${COL_RED}${HH_PLAYED} hs ${MM_PLAYED} mins" >> "${VLC_STATS_HISTORY_FILE}"
  log.info "Files played count: ${COL_RED}${NUM_FILES_PLAYED}" >> "${VLC_STATS_HISTORY_FILE}"
  logTemperature  >> "${VLC_STATS_HISTORY_FILE}"
  logVlcCpuUsage >> "${VLC_STATS_HISTORY_FILE}"
}

trimStatsHistoryFile() {
  local RUN_START_LINE=`cat "${VLC_STATS_HISTORY_FILE}" | grep -n "VLC run stats:" | cut -d ':' -f 1 | tail -n ${NUM_STAT_ITERATIONS_TO_KEEP} | head -n 1`
  if [ -z "${RUN_START_LINE}" ]; then
    RUN_START_LINE=0
  fi
  local VLC_STATS_HISTORY_FILE_TAIL=`cat "${VLC_STATS_HISTORY_FILE}" | tail -n +${RUN_START_LINE}`
  echo -ne "" > "${VLC_STATS_HISTORY_FILE}"
  echo -e "${VLC_STATS_HISTORY_FILE_TAIL}" >> "${VLC_STATS_HISTORY_FILE}"
}

showStatsHistoryFile() {
  if [ "${NUMBER_OF_STAT_ITERATIONS}" == "0" ]; then
    log.info "cat ${VLC_STATS_HISTORY_FILE}"
    cat "${VLC_STATS_HISTORY_FILE}"
    return
  fi
  local RUN_START_LINE=`cat "${VLC_STATS_HISTORY_FILE}" | grep -n "VLC run stats:" | cut -d ':' -f 1 | tail -n ${NUMBER_OF_STAT_ITERATIONS} | head -n 1`
  if [ -z "${RUN_START_LINE}" ]; then
    RUN_START_LINE=0
  fi
  log.trace "RUN_START_LINE=${RUN_START_LINE}"
  log.info "cat "${VLC_STATS_HISTORY_FILE}" | tail -n +${RUN_START_LINE}"
  cat "${VLC_STATS_HISTORY_FILE}" | tail -n +${RUN_START_LINE} 
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
      -n)
        NUMBER_OF_STAT_ITERATIONS="${CURRENT_OPTION_ARG}"
        ;;
      -s|--show-current-stats-only)
        SHOW_CURRENT_STATS_ONLY=true
        ;;
      -t|--show-history-file-only)
        SHOW_HISTORY_FILE_ONLY=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-n [val]" "number of stats iterations to print. Default is to print entire stats file"
  addHelpOption "-s --show-current-stats-only" "skip the stats file update. just print the current stats to the console"
  addHelpOption "-t --show-history-file-only" "trace. just show the existing stats file without any calculations"
}

main "$@"
