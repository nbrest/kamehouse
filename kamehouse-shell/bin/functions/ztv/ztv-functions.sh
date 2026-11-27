# kamehouse ztv-functions.sh

FILE_TO_PLAY=""

# Configure ZTV Player application settings to log debug to this file
ZTV_LOG_FILE="${HOME}/.kamehouse/var/log/ztv.log"

ZTV_DATA_PATH="${HOME}/.kamehouse/data/ztv"
mkdir -p "${ZTV_DATA_PATH}"

ZTV_PROCESS_INFO_FILE="${ZTV_DATA_PATH}/ztv-process.info"
ZTV_STATS_HISTORY_FILE=${ZTV_DATA_PATH}/ztv-process.stats
ZTV_CURRENT_RUN_PLAYED_FILES=${ZTV_DATA_PATH}/ztv-process-played-files.list

ZTV_PROCESS_START_DATE=""
ZTV_CURRENT_FILE_LOADED=""

ZTV_PORT="8080"

ZTV_PID=""
ZTV_IS_RUNNING=false

ZTV_PARAMS=""

ZTV_STATS_MEDIA_FILES="\.mp3|\.MP3|\.mp4|\.MP4|\.mkv|\.MKV|\.m3u|\.M3U"

checkExistingZtvLogFile() {
  if [ ! -f "${ZTV_LOG_FILE}" ]; then
    log.error "${ZTV_LOG_FILE} doesn't exist. Check ztv player configuration"
    exitProcess ${EXIT_INVALID_CONFIG}
  fi
}

rotateZtvLog() {
  log.trace "Rotating ztv logs"
  if [ -f "${ZTV_LOG_FILE}" ]; then
    mv ${ZTV_LOG_FILE} ${ZTV_LOG_FILE}.old
  fi
  echo "" > ${ZTV_LOG_FILE}
}

setZtvProcessInfo() {
  log.debug "Setting ztv process info"
  echo "# ztv process info generated on $(date +%Y-%m-%d' '%H:%M:%S)" > ${ZTV_PROCESS_INFO_FILE}
  echo "ZTV_PROCESS_START_DATE=\"$(date +%Y-%m-%d' '%H:%M:%S)\"" >> ${ZTV_PROCESS_INFO_FILE}
  echo "ZTV_CURRENT_FILE_LOADED=\"${FILE_TO_PLAY}\"" >> ${ZTV_PROCESS_INFO_FILE}
}

removeZtvProcessInfo() {
  if [ -f "${ZTV_PROCESS_INFO_FILE}" ]; then
    rm ${ZTV_PROCESS_INFO_FILE}
  fi
}

searchForActiveZtvProcess() {
  log.debug "Searching for ZTV process with an http server"
  if ${IS_LIN_HOST}; then
    ZTV_PID=`netstat -nltp 2>/dev/null | grep ":${ZTV_PORT} " | grep vlc | grep -v tcp6 | awk '{print $7}' | cut -d '/' -f 1`
  else
    ZTV_PID=`netstat -ano | grep "LISTENING" | grep "\[::\]:${ZTV_PORT} " | tail -n 1 | awk '{print $5}' | cut -d '/' -f 1`
  fi
  log.debug "ZTV_PID: ${ZTV_PID}"
  if [ -z "${ZTV_PID}" ]; then
    log.debug "ZTV Player is not running"
    ZTV_IS_RUNNING=false
  else
    log.debug "ZTV Player is running with pid ${COL_PURPLE}${ZTV_PID}"
    ZTV_IS_RUNNING=true
  fi
}

checkRunningZtvProcess() {
  searchForActiveZtvProcess
  if ! ${ZTV_IS_RUNNING}; then
    log.info "ZTV Player is not running. Clearing ztv process files"
    echo "" > ${ZTV_CURRENT_RUN_PLAYED_FILES}
    removeZtvProcessInfo
    rotateZtvLog
    exitSuccessfully
  fi
}
