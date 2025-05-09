#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

initScriptEnv() {
  PLAYLIST_FILE=""
}

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
