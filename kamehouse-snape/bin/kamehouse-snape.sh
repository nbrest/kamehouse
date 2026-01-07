#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  SNAPE_SCRIPT=""
  SNAPE_SCRIPT_ARGS=
}

mainProcess() {
  log.info "Executing snape script ${COL_PURPLE}${SNAPE_SCRIPT}${COL_DEFAULT_LOG} with args ${COL_PURPLE}${SNAPE_SCRIPT_ARGS}"
  python ${SNAPE_PATH}/${SNAPE_SCRIPT} ${SNAPE_SCRIPT_ARGS}
  exitProcess $?
}

parseArguments() {
  SNAPE_SCRIPT=$1
  shift
  SNAPE_SCRIPT_ARGS=$@
}

setEnvFromArguments() {
  checkRequiredOption "snape script" "${SNAPE_SCRIPT}" 
  if [[ ! "${SNAPE_SCRIPT}" =~ .*\.py$ ]]; then
    log.error "Invalid snape script. Needs to end in .py"
    exit ${EXIT_INVALID_ARG}
  fi
}

printHelpOptions() {
  addHelpOption "snape-script.py [args]" "Snape script and its optional arguments" "r"
}

parseHelp() {
  printHelp
  # continue to python script
}

main "$@"
