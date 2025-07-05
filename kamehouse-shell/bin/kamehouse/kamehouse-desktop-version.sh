#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

mainProcess() {
  local CMD_VERSION_FILE="${HOME}/programs/kamehouse-desktop/conf/build-info.json"
  cat "${CMD_VERSION_FILE}"
}

main "$@"
