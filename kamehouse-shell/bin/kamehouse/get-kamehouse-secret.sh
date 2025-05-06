#!/bin/bash

# Disable logs
LOG=ERROR
SKIP_LOG_START_FINISH=true

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

LOAD_KAMEHOUSE_SECRETS=true
LOG_PROCESS_TO_FILE=false
KAMEHOUSE_SECRET_NAME=""

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
