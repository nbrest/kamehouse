#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  if ${KEEP_ALIVE_SCRIPTS_DISABLED}; then
    log.info "keep alive scripts are ${COL_RED}disabled${COL_DEFAULT_LOG} in kamehouse.cfg"
  else
    log.info "keep alive scripts are ${COL_YELLOW}enabled${COL_DEFAULT_LOG} in kamehouse.cfg"
  fi
}

main "$@"
