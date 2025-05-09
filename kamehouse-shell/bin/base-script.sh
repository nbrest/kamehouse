#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99
fi

initKameHouseShellEnv() {
  #LOG=DISABLED
  #LOG_PROCESS_TO_FILE=false
  #LOG_CMD_ARGS=false
  #LOAD_KAMEHOUSE_SECRETS=true
  return
}

initScriptEnv() {
  TEST_PARAM=""
}

mainProcess() {
  log.info "base script: TEST_PARAM=${TEST_PARAM}"
  printHelp
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
