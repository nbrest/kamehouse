#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  KAMEHOUSE_CFG_TEMP="kamehouse-*.cfg"
}

mainProcess() {
  log.info "ls -lh ${HOME}/.kamehouse/var/tmp/${KAMEHOUSE_CFG_TEMP}"
  ls -lh ${HOME}/.kamehouse/var/tmp/${KAMEHOUSE_CFG_TEMP}
  
  log.info "Removing temp configs"
  rm -f ${HOME}/.kamehouse/var/tmp/${KAMEHOUSE_CFG_TEMP}

  log.info "ls -lh ${HOME}/.kamehouse/var/tmp/${KAMEHOUSE_CFG_TEMP}"
  ls -lh ${HOME}/.kamehouse/var/tmp/${KAMEHOUSE_CFG_TEMP}
}

main "$@"
