#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
  LOAD_KAMEHOUSE_SECRETS=true
}

mainProcess() {
  log.info "mariadb -u root -p***"
  mariadb -u root -p${MARIADB_PASS_ROOT_WIN}
}

main "$@"
