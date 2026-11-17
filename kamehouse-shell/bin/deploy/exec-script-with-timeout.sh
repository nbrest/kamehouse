#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  SCRIPT=""
  SCRIPT_ARGS=()
}

mainProcess() {
  log.debug "SCRIPT_MAX_RUNTIME=${SCRIPT_MAX_RUNTIME}"
  execWithTimeout "${SCRIPT_MAX_RUNTIME}" "${SCRIPT}" "${SCRIPT_ARGS[@]}"
}

parseArguments() {
  local OPTIONS=("$@")
  if [ ${#OPTIONS[@]} -eq 0 ]; then
    return
  fi
  SCRIPT="${OPTIONS[0]}"
  SCRIPT_ARGS=("${OPTIONS[@]:1}")
}

setEnvFromArguments() {
  checkRequiredOption "script" "${SCRIPT}"
}

printHelpFooter() {
  echo -e ""
  echo -e "${COL_YELLOW}   > Usage: ${SCRIPT_NAME} [options] <script> [args]${COL_NORMAL}"
  echo -e ""
}

main "$@"
