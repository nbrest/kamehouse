# kamehouse vlc-functions.sh

FILE_TO_PLAY=""

VLC_LOG_FILE="${HOME}/logs/vlc.log"

VLC_DATA_PATH="${HOME}/programs/kamehouse-shell/data/vlc"
VLC_PROCESS_INFO_FILE="${VLC_DATA_PATH}/vlc-process.info"

VLC_PROCESS_START_DATE=""
VLC_CURRENT_FILE_LOADED=""

VLC_PORT="8080"

VLC_PID=""
VLC_IS_RUNNING=false

mkdir -p "${VLC_DATA_PATH}"

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
    netstat -nltp | grep ":${VLC_PORT} " | grep vlc | grep -v tcp6 | awk '{print $7}' | cut -d '/' -f 1
    VLC_PID=`netstat -nltp | grep ":${VLC_PORT} " | grep vlc | grep -v tcp6 | awk '{print $7}' | cut -d '/' -f 1`
  else
    netstat -ano | grep "LISTENING" | grep "\[::\]:${VLC_PORT} " | tail -n 1
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
