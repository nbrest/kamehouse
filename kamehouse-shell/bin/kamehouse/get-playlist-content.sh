#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

LOG=DISABLED

mainProcess() {
  if ${IS_LINUX_HOST}; then
    cat "${PLAYLIST_FILE}"
  else
    PLAYLIST_FILE="`sed 's#/#\\\#Ig' <<<"${PLAYLIST_FILE}"`"
    PLAYLIST_CONTENT=`powershell.exe -c "cat ${PLAYLIST_FILE}"`
    PLAYLIST_CONTENT="`sed 's#\\\#/#Ig' <<<"${PLAYLIST_CONTENT}"`"
    echo -e "${PLAYLIST_CONTENT}"
  fi
}

setInitialGlobalEnv() {
  PLAYLIST_FILE=""
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
        PLAYLIST_FILE="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  checkRequiredOption "-f" "${PLAYLIST_FILE}" 
}

printHelpOptions() {
  addHelpOption "-f file" "playlist file to load content from" "r"
}

main "$@"
