#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

FILE_TO_PLAY=""
START_FROM_SSH=false

mainProcess() {
  if ${START_FROM_SSH}; then
    ${HOME}/programs/kamehouse-shell/bin/win/vlc/vlc-start-from-ssh.sh -f "${FILE_TO_PLAY}"
    exitSuccessfully
  fi
  FILE_TO_PLAY="`sed 's#"##Ig' <<<"${FILE_TO_PLAY}"`"
  log.info "Playing file ${FILE_TO_PLAY}"
  vlc.exe ${FILE_TO_PLAY} &
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
      --start-from-ssh)
        START_FROM_SSH=true
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
  addHelpOption "--start-from-ssh" "Use this when starting vlc from ssh from a remote docker container"
}

main "$@"
