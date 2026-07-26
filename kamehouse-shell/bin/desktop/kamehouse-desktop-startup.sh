#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
  LOG_PROCESS_TO_FILE=false
}

mainProcessPre() {
  # Execute the latest deployed version of kamehouse-desktop
  ${HOME}/programs/kamehouse-desktop/bin/kamehouse-desktop-startup.sh "$@"
}

parseArguments() {
  return
}

main "$@"
