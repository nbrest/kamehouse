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
  setupLinuxEnvironment
  rotateVlcLog
  FILE_TO_PLAY="`sed 's#"##Ig' <<<"${FILE_TO_PLAY}"`"
  log.info "Playing file ${FILE_TO_PLAY}"
  setVlcProcessInfo
  setVlcParams
  vlc ${FILE_TO_PLAY} ${VLC_PARAMS} &
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
