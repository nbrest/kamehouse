#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/ztv/ztv-functions.sh

initScriptEnv() {
  NUM_STAT_ITERATIONS_TO_KEEP=10000
  NUMBER_OF_STAT_ITERATIONS=0
  SHOW_CURRENT_STATS_ONLY=false
  SHOW_HISTORY_FILE_ONLY=false

  let TOTAL_MINS_PLAYED=0
  let NUM_FILES_PLAYED=0
  let HH_PLAYED=0
  let MM_PLAYED=0
}

mainProcess() {
  checkRunningZtvProcess
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
  if [ ! -f "${ZTV_STATS_HISTORY_FILE}" ]; then
    log.info "Initializing file ${ZTV_STATS_HISTORY_FILE}"
    echo -ne "" > "${ZTV_STATS_HISTORY_FILE}"
  fi
}

calculateCurrentRunStats() {
  if [ ! -f "${ZTV_PROCESS_INFO_FILE}" ]; then
    log.error "${ZTV_PROCESS_INFO_FILE} file not found. Can't calculate current run stats. Restart the ztv player through kamehouse"
    exitProcess ${EXIT_ERROR}
  fi  
  source "${ZTV_PROCESS_INFO_FILE}"
  local CURRENT_DATE_SECONDS="$(date +%s)"
  local ZTV_PROCESS_START_DATE_SECONDS="$(date -d "${ZTV_PROCESS_START_DATE}" +%s)"
  TOTAL_MINS_PLAYED=$((CURRENT_DATE_SECONDS - ZTV_PROCESS_START_DATE_SECONDS))
  TOTAL_MINS_PLAYED=$((TOTAL_MINS_PLAYED / 60)) 
  log.trace "TOTAL_MINS_PLAYED=${TOTAL_MINS_PLAYED}"
  HH_PLAYED=$((TOTAL_MINS_PLAYED / 60)) 
  MM_PLAYED=$((HH_PLAYED * 60)) 
  if [ "${MM_PLAYED}" != "0" ]; then
    MM_PLAYED=${MM_PLAYED#0}
  fi
  MM_PLAYED=$((TOTAL_MINS_PLAYED - MM_PLAYED)) 

  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/ztv/ztv-get-current-run-played-files.sh --quiet-mode
  LAST_PLAYED_FILE=`tail -n 1 "${ZTV_CURRENT_RUN_PLAYED_FILES}" | sed 's#\\\#/#Ig'`
  NUM_FILES_PLAYED=`cat "${ZTV_CURRENT_RUN_PLAYED_FILES}" | wc -l`
  NUM_FILES_PLAYED=$((NUM_FILES_PLAYED - 1)) # remove .m3u from count
}

showCurrentRunStats() {
  log.info "${COL_RED}**************************************************"
  log.info "${COL_YELLOW}ZTV run stats:"
  log.info "ZTV load file: ${COL_PURPLE}${ZTV_CURRENT_FILE_LOADED}"
  log.info "Current file: ${COL_PURPLE}${LAST_PLAYED_FILE}"
  log.info "ZTV run start: ${COL_RED}${ZTV_PROCESS_START_DATE}"
  log.info "ZTV run time:  ${COL_RED}${HH_PLAYED} hs ${MM_PLAYED} mins"
  log.info "Files played count:  ${COL_RED}${NUM_FILES_PLAYED}"
  logTemperature
  logZtvCpuUsage
}

logZtvCpuUsage() {
  if ! ${IS_LIN_HOST}; then
    return
  fi
  local ZTV_PID=`psListProcesses | grep vlc | grep -v "ztv-start.sh" | grep -E ".*(${ZTV_STATS_MEDIA_FILES}).*" | awk -v pidField="${PS_PID_FIELD}" '{print $pidField}'`
  local ZTV_CPU_USAGE=`top -e m -E m -p "${ZTV_PID}" -n 1 -b | grep vlc | awk '{print $9}'`
  log.info "ZTV cpu usage: ${COL_RED}${ZTV_CPU_USAGE}%"
}

logTemperature() {
  if ! ${IS_LIN_HOST}; then
    return
  fi
  local TEMPERATURE=`vcgencmd measure_temp | grep "temp="`
  log.info "Temperature: ${COL_RED}${TEMPERATURE}"
}

updateStatsHistoryFile() {
  trimStatsHistoryFile
  log.info "${COL_RED}**************************************************" >> "${ZTV_STATS_HISTORY_FILE}"
  log.info "${COL_YELLOW}ZTV run stats:" >> "${ZTV_STATS_HISTORY_FILE}"
  log.info "ZTV load file: ${COL_PURPLE}${ZTV_CURRENT_FILE_LOADED}" >> "${ZTV_STATS_HISTORY_FILE}"
  log.info "Current file:  ${COL_PURPLE}${LAST_PLAYED_FILE}" >> "${ZTV_STATS_HISTORY_FILE}"
  log.info "ZTV run start: ${COL_RED}${ZTV_PROCESS_START_DATE}" >> "${ZTV_STATS_HISTORY_FILE}"
  log.info "ZTV run time:  ${COL_RED}${HH_PLAYED} hs ${MM_PLAYED} mins" >> "${ZTV_STATS_HISTORY_FILE}"
  log.info "Files played count: ${COL_RED}${NUM_FILES_PLAYED}" >> "${ZTV_STATS_HISTORY_FILE}"
  logTemperature  >> "${ZTV_STATS_HISTORY_FILE}"
  logZtvCpuUsage >> "${ZTV_STATS_HISTORY_FILE}"
}

trimStatsHistoryFile() {
  local RUN_START_LINE=`cat "${ZTV_STATS_HISTORY_FILE}" | grep -n "ZTV run stats:" | cut -d ':' -f 1 | tail -n ${NUM_STAT_ITERATIONS_TO_KEEP} | head -n 1`
  if [ -z "${RUN_START_LINE}" ]; then
    RUN_START_LINE=0
  fi
  local ZTV_STATS_HISTORY_FILE_TAIL=`cat "${ZTV_STATS_HISTORY_FILE}" | tail -n +${RUN_START_LINE}`
  echo -ne "" > "${ZTV_STATS_HISTORY_FILE}"
  echo -e "${ZTV_STATS_HISTORY_FILE_TAIL}" >> "${ZTV_STATS_HISTORY_FILE}"
}

showStatsHistoryFile() {
  if [ "${NUMBER_OF_STAT_ITERATIONS}" == "0" ]; then
    log.info "cat ${ZTV_STATS_HISTORY_FILE}"
    cat "${ZTV_STATS_HISTORY_FILE}"
    return
  fi
  local RUN_START_LINE=`cat "${ZTV_STATS_HISTORY_FILE}" | grep -n "ZTV run stats:" | cut -d ':' -f 1 | tail -n ${NUMBER_OF_STAT_ITERATIONS} | head -n 1`
  if [ -z "${RUN_START_LINE}" ]; then
    RUN_START_LINE=0
  fi
  log.trace "RUN_START_LINE=${RUN_START_LINE}"
  log.info "tail -n +${RUN_START_LINE} ${ZTV_STATS_HISTORY_FILE}"
  tail -n +${RUN_START_LINE} "${ZTV_STATS_HISTORY_FILE}"
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
