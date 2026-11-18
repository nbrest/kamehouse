#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  ALLOW_PARALLEL_RUNS=true
  COMMAND_TIMEOUT=0
  COMMAND=""
  COMMAND_ARGS=()
}

mainProcess() {
  execWithTimeout ${COMMAND_TIMEOUT} "${COMMAND}" "${COMMAND_ARGS[@]}"
}

parseArguments() {
  local OPTIONS=("$@")
  if [ ${#OPTIONS[@]} -eq 0 ]; then
    return
  fi
  COMMAND_TIMEOUT="${OPTIONS[0]}"
  COMMAND="${OPTIONS[1]}"
  COMMAND_ARGS=("${OPTIONS[@]:2}")
}

setEnvFromArguments() {
  checkRequiredOption "COMMAND" "${COMMAND}"
}

printHelpFooter() {
  echo -e ""
  echo -e "${COL_YELLOW}   > Usage: ${SCRIPT_NAME} <timeout-seconds> <command> [command args]${COL_NORMAL}"
  echo -e ""
}

main "$@"
