#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  #LOG=DISABLED
  #LOG_PROCESS_TO_FILE=false
  #LOG_CMD_ARGS=false
  #LOAD_KAMEHOUSE_SECRETS=true
  return
}

setDefaultScriptConfig() {
  # Set a script property to override in the script's own config file
  SAMPLE_SCRIPT_CFG_VAR=default-value
}

initScriptEnv() {
  TEST_PARAM=""
}

mainProcessLin() {
  log.info "base script lin: TEST_PARAM=${TEST_PARAM}"
  log.info "SAMPLE_SCRIPT_CFG_VAR=${SAMPLE_SCRIPT_CFG_VAR}"
  printHelp
}

mainProcessWin() {
  log.info "base script win: TEST_PARAM=${TEST_PARAM}"
  log.info "SAMPLE_SCRIPT_CFG_VAR=${SAMPLE_SCRIPT_CFG_VAR}"
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
      -t|--test-param)
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
  addHelpOption "-t --test-param [val]" "Test parameter" "r"
}

main "$@"
