#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  return
}

mainProcess() {
  setupLinuxEnvironment
  log.info "Starting ${COL_PURPLE}kamehouse-desktop${COL_DEFAULT_LOG}"
  if ${IS_LINUX_HOST}; then
    # start compositor in raspberry pi
    export DISPLAY=${DISPLAY} picom &
    #xcompmgr &
  fi
  python ${SNAPE_PATH}/kamehouse-desktop/kamehouse-desktop.py
}

main "$@"
