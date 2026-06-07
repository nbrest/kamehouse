#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/vlc/vlc-functions.sh
importKamehouse functions/vlc/vlc-start-functions.sh

setDefaultScriptConfig() {
  setDefaultScriptConfigVlcStart
}

mainProcess() {
  if ${IS_LINUX_HOST}; then
    processLin
  else
    processWin
  fi
}

processLin() {
  setupLinuxEnvironment
  rotateVlcLog
  FILE_TO_PLAY="`sed 's#"##Ig' <<<"${FILE_TO_PLAY}"`"
  log.info "Playing file ${FILE_TO_PLAY}"
  setVlcProcessInfo
  setVlcStartParams
  vlc ${FILE_TO_PLAY} ${VLC_PARAMS} &
}

processWin() {
  rotateVlcLog
  FILE_TO_PLAY="`sed 's#"##Ig' <<<"${FILE_TO_PLAY}"`"
  log.info "Playing file ${FILE_TO_PLAY}"
  setVlcProcessInfo
  setVlcStartParams
  local WINDOWS_FILE_RX=^[A-Za-z]:/.*
  if [[ "${FILE_TO_PLAY}" =~ ${WINDOWS_FILE_RX} ]]; then
    log.info "Playing a local windows file, rewriting paths to windows"
    FILE_TO_PLAY="`sed 's#/#\\\#Ig' <<<"${FILE_TO_PLAY}"`"
  fi
  vlc.exe ${FILE_TO_PLAY} ${VLC_PARAMS}
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
