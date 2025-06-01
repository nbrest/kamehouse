# kamehouse vlc-functions.sh

FILE_TO_PLAY=""

# Configure vlc application settings to log debug to this file
VLC_LOG_FILE="${HOME}/logs/vlc.log"

VLC_DATA_PATH="${HOME}/.kamehouse/data/vlc"
mkdir -p "${VLC_DATA_PATH}"

VLC_PROCESS_INFO_FILE="${VLC_DATA_PATH}/vlc-process.info"
VLC_STATS_HISTORY_FILE=${VLC_DATA_PATH}/vlc-process.stats
VLC_CURRENT_RUN_PLAYED_FILES=${VLC_DATA_PATH}/vlc-process-played-files.list

VLC_PROCESS_START_DATE=""
VLC_CURRENT_FILE_LOADED=""

VLC_PORT="8080"

VLC_PID=""
VLC_IS_RUNNING=false

VLC_PARAMS=""

VLC_STATS_MEDIA_FILES="\.mp3|\.MP3|\.mp4|\.MP4|\.mkv|\.MKV|\.m3u|\.M3U"

checkExistingVlcLogFile() {
  if [ ! -f "${VLC_LOG_FILE}" ]; then
    log.error "${VLC_LOG_FILE} doesn't exist. Check vlc configuration"
    exitProcess ${EXIT_INVALID_CONFIG}
  fi
}

rotateVlcLog() {
  log.trace "Rotating vlc logs"
  if [ -f "${VLC_LOG_FILE}" ]; then
    mv ${VLC_LOG_FILE} ${VLC_LOG_FILE}.old
  fi
  echo "" > ${VLC_LOG_FILE}
}

setVlcProcessInfo() {
  log.debug "Setting vlc process info"
  echo "# vlc process info generated on $(date +%Y-%m-%d' '%H:%M:%S)" > ${VLC_PROCESS_INFO_FILE}
  echo "VLC_PROCESS_START_DATE=\"$(date +%Y-%m-%d' '%H:%M:%S)\"" >> ${VLC_PROCESS_INFO_FILE}
  echo "VLC_CURRENT_FILE_LOADED=\"${FILE_TO_PLAY}\"" >> ${VLC_PROCESS_INFO_FILE}
}

removeVlcProcessInfo() {
  if [ -f "${VLC_PROCESS_INFO_FILE}" ]; then
    rm ${VLC_PROCESS_INFO_FILE}
  fi  
}

searchForActiveVlcProcess() {
  log.debug "Searching for vlc process with an http server"
  if ${IS_LINUX_HOST}; then
    VLC_PID=`netstat -nltp 2>/dev/null | grep ":${VLC_PORT} " | grep vlc | grep -v tcp6 | awk '{print $7}' | cut -d '/' -f 1`
  else
    VLC_PID=`netstat -ano | grep "LISTENING" | grep "\[::\]:${VLC_PORT} " | tail -n 1 | awk '{print $5}' | cut -d '/' -f 1`
  fi
  log.debug "VLC_PID: ${VLC_PID}" 
  if [ -z "${VLC_PID}" ]; then
    log.debug "Vlc is not running"
    VLC_IS_RUNNING=false
  else
    log.debug "Vlc is running with pid ${COL_PURPLE}${VLC_PID}"
    VLC_IS_RUNNING=true
  fi
}

checkRunningVlcProcess() {
  searchForActiveVlcProcess
  if ! ${VLC_IS_RUNNING}; then
    log.info "Vlc is not running. Clearing vlc process files"
    echo "" > ${VLC_CURRENT_RUN_PLAYED_FILES}
    removeVlcProcessInfo
    rotateVlcLog
    exitSuccessfully
  fi  
}

setVlcParams() {
  log.debug "Setting vlc params"
  local FILE_EXT=${FILE_TO_PLAY: -3}
  local MUSIC_PLAYLIST_RX=.*${PLAYLISTS_PATH}/music/.*
  local IS_MUSIC_PLAYLIST=false

  if [ "${FILE_EXT}" == "mp3" ]; then
    log.debug "File to play is an mp3"
    IS_MUSIC_PLAYLIST=true
  fi
  
  if [[ "${FILE_TO_PLAY}" =~ ${MUSIC_PLAYLIST_RX} ]]; then
    log.debug "File to play is a music playlist"
    IS_MUSIC_PLAYLIST=true
  fi

  if ${IS_MUSIC_PLAYLIST}; then
    if ${VLC_MUSIC_USE_VISUALIZER}; then
      log.info "Using visualizer filter. The filter needs to be enabled also on vlc player config"
      VLC_PARAMS="${VLC_PARAMS} --effect-list=spectrum"
    else
      log.info "Starting vlc minimized. The visualization filter needs to be disabled on vlc player config"
      VLC_PARAMS="${VLC_PARAMS} --qt-start-minimized --qt-system-tray"
    fi
  fi
}
