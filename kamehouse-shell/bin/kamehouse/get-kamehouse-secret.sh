#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

initKameHouseShellEnv() {
  LOG=DISABLED
  LOG_PROCESS_TO_FILE=false
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  KAMEHOUSE_SECRET_NAME=""
}

mainProcess() {
  echo ${!KAMEHOUSE_SECRET_NAME}
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
      -s)
        KAMEHOUSE_SECRET_NAME="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  checkRequiredOption "-s" "${KAMEHOUSE_SECRET_NAME}" 
}

printHelpOptions() {
  addHelpOption "-s KAMEHOUSE_SECRET_NAME" "Name of the kamehouse secret to retrieve" "r"
}

main "$@"
