#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  if ${KEEP_ALIVE_SCRIPTS_DISABLED}; then
    log.info "keep alive scripts are ${COL_RED}disabled${COL_DEFAULT_LOG} in kamehouse.cfg. Switching to ${COL_YELLOW}enabled"
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/config/update-kamehouse-config.sh -k "KEEP_ALIVE_SCRIPTS_DISABLED" -v "false"
  else
    log.info "keep alive scripts are ${COL_YELLOW}enabled${COL_DEFAULT_LOG} in kamehouse.cfg. Switching to ${COL_RED}disabled"
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/config/update-kamehouse-config.sh -k "KEEP_ALIVE_SCRIPTS_DISABLED" -v "true"
  fi
  ${HOME}/programs/kamehouse-shell/bin/common/keep-alive/keep-alive-status.sh 
}

main "$@"
