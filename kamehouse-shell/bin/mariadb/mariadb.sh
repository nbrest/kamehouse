#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
  LOAD_KAMEHOUSE_SECRETS=true
}

mainProcessLin() {
  sudo mariadb
}

mainProcessWin() {
  log.info "mariadb -u root -p***"
  mariadb -u root -p${MARIADB_PASS_ROOT_WIN}
}

main "$@"
