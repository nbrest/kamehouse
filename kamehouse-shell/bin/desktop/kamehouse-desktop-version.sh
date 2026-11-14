#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

mainProcess() {
  local VERSION_FILE="${HOME}/.kamehouse/opt/kamehouse-desktop/conf/build-info.json"
  cat "${VERSION_FILE}"
}

main "$@"
