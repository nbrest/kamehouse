#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
  LOG_PROCESS_TO_FILE=false
}

initScriptEnv() {
  ALLOW_PARALLEL_RUNS=true
}

mainProcess() {
  # Execute the latest deployed version of kamehouse-cmd
  ${HOME}/.kamehouse/opt/kamehouse-cmd/bin/kamehouse-cmd.sh "$@"
}

parseArguments() {
  return
}

main "$@"
