#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

FILE_TO_PLAY=""

mainProcess() {
  FILE_TO_PLAY="`sed 's#"##Ig' <<<"${FILE_TO_PLAY}"`"
  log.info "Playing file ${FILE_TO_PLAY}"
  if [ -z "${DISPLAY}" ]; then
    export DISPLAY=:0.0
  fi
  if [ -z "${XDG_RUNTIME_DIR}" ]; then
    export XDG_RUNTIME_DIR=/run/user/$(id -u)
  fi
  vlc ${FILE_TO_PLAY} >> /dev/null 2>&1  
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
