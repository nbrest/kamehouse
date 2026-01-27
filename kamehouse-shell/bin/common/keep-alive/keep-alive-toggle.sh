#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  if ${KEEP_ALIVE_SCRIPTS_DISABLED}; then
    log.info "keep alive scripts are disabled in kamehouse.cfg. Switching to enabled"
    updateKameHouseConfig KEEP_ALIVE_SCRIPTS_DISABLED false
  else
    log.info "keep alive scripts are enabled in kamehouse.cfg. Switching to disabled"
    updateKameHouseConfig KEEP_ALIVE_SCRIPTS_DISABLED true
  fi
  ${HOME}/programs/kamehouse-shell/bin/common/keep-alive/keep-alive-status.sh 
}

main "$@"
