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

mainProcess() {
  checkRunningVlcProcess
  showCurrentRunPlayedFilesSorted
  showVlcLogsFiltered
  showCurrentRunPlayedFilesHeadTail
  showVlcSystemProcessInfo
  showVlcStats
  showVlcLogsLastDrm
}

showCurrentRunPlayedFilesSorted() {
  ${HOME}/programs/kamehouse-shell/bin/common/vlc/vlc-get-current-run-played-files.sh --sort-alpha
}

showVlcLogsFiltered() {
  log.info "tail filtered vlc logs"
  local LAST_M3U_LINE=`grep -n -e "main debug:  (path:.*.m3u" ${VLC_LOG_FILE} | cut -d ':' -f 1 | tail -n 1`
  local TAIL_GREP_REGEX="drm_vout.*|main debug:  \(path:..*(\.mp4|\.MP4|\.mkv|\.MKV|\.m3u|\.M3U).*"
  echo -ne "${COL_RED}"
  tail -n +${LAST_M3U_LINE} ${VLC_LOG_FILE} | grep -E "${TAIL_GREP_REGEX}" | tail -n 300
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
  local VLC_PID=`ps -ef | grep vlc | grep -E ".*(\.mp4|\.MP4|\.mkv|\.MKV|\.m3u|\.M3U).*" | awk '{print $2}'`
  local TOP_OUTPUT=`top -e m -E m -p "${VLC_PID}" -n 1 -b`
  echo "${TOP_OUTPUT}"

  log.info "ps -ef | grep vlc"
  ps -ef | grep vlc | grep -E ".*(\.mp4|\.MP4|\.mkv|\.MKV|\.m3u|\.M3U).*"
}  

showVlcStats() {
  ${HOME}/programs/kamehouse-shell/bin/common/vlc/vlc-stats.sh -n 8
}

showVlcLogsLastDrm() {
  local LAST_M3U_LINE=`grep -n -e "main debug:  (path:.*.m3u" ${VLC_LOG_FILE} | cut -d ':' -f 1 | tail -n 1`
  local TAIL_GREP_REGEX="drm_vout debug: OK simple pic test.*|drm_vout debug: get_lease_fd OK.*|drm_vout error: Failed to get xlease.*|main debug:  \(path:..*(\.mp4|\.MP4|\.mkv|\.MKV|\.m3u|\.M3U).*"
  log.info "Last drm filtered entries of vlc.log"
  tail -n +${LAST_M3U_LINE} ${VLC_LOG_FILE} | grep -E "${TAIL_GREP_REGEX}" | tail -n 10
}

main "$@"
