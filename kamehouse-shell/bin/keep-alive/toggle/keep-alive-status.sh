#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  ALLOW_PARALLEL_RUNS=true
}

mainProcess() {
  if ${KEEP_ALIVE_SCRIPTS_DISABLED}; then
    log.info "keep alive scripts are ${COL_RED}disabled${COL_MESSAGE} in kamehouse.cfg"
  else
    log.info "keep alive scripts are ${COL_YELLOW}enabled${COL_MESSAGE} in kamehouse.cfg"
  fi
}

main "$@"
