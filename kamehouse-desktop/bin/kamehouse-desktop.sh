#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  return
}

mainProcess() {
  log.info "Starting ${COL_PURPLE}kamehouse-desktop${COL_DEFAULT_LOG}"
  python ${SNAPE_PATH}/kamehouse-desktop/kamehouse-desktop.py
}

main "$@"
