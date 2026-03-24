#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  KAMEHOUSE_CFG_TEMP="kamehouse-*.cfg"
}

mainProcess() {
  log.info "ls -lh ${HOME}/temp/${KAMEHOUSE_CFG_TEMP}"
  ls -lh ${HOME}/temp/${KAMEHOUSE_CFG_TEMP}
  
  log.info "Removing temp configs"
  rm -f ${HOME}/temp/${KAMEHOUSE_CFG_TEMP}

  log.info "ls -lh ${HOME}/temp/${KAMEHOUSE_CFG_TEMP}"
  ls -lh ${HOME}/temp/${KAMEHOUSE_CFG_TEMP}
}

main "$@"
