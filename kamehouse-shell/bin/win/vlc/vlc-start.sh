#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

FILE_TO_PLAY=""
VLC_LOG_FILE="${HOME}/logs/vlc.log"

mainProcess() {
  rotateVlcLog
  FILE_TO_PLAY="`sed 's#"##Ig' <<<"${FILE_TO_PLAY}"`"
  log.info "Playing file ${FILE_TO_PLAY}"
  local WINDOWS_FILE_RX=^[A-Za-z]:/.*
  if [[ "${FILE_TO_PLAY}" =~ ${WINDOWS_FILE_RX} ]]; then
    log.info "Playing a local windows file, rewriting paths to windows"
    FILE_TO_PLAY="`sed 's#/#\\\#Ig' <<<"${FILE_TO_PLAY}"`"
  fi
  vlc.exe ${FILE_TO_PLAY}
}

rotateVlcLog() {
  log.trace "Rotating vlc logs"
  if [ -f "${VLC_LOG_FILE}" ]; then
    mv ${VLC_LOG_FILE} ${VLC_LOG_FILE}.old
  fi
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
      -f)
        FILE_TO_PLAY="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  checkRequiredOption "-f" "${FILE_TO_PLAY}" 
}

printHelpOptions() {
  addHelpOption "-f file" "File to play" "r"
}

main "$@"
