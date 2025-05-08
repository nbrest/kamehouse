#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

#LOG=DISABLED
#LOG_PROCESS_TO_FILE=false
#LOG_CMD_ARGS=false
#LOAD_KAMEHOUSE_SECRETS=true

mainProcess() {
  log.info "base script: TEST_PARAM=${TEST_PARAM}"
  printHelp
}

setInitialGlobalEnv() {
  TEST_PARAM=""
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
      -t)
        TEST_PARAM="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  checkRequiredOption "-t" "${TEST_PARAM}" 
}

printHelpOptions() {
  addHelpOption "-t testParam" "test param" "r"
}

main "$@"
