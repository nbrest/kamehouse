#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  SQL_FILE=${HOME}/programs/kamehouse-shell/sql/mariadb/status-kamehouse.sql
}

mainProcess() {
  log.info "KameHouse database status"
  mariadb --force --table -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} kamehouse < ${SQL_FILE}
}

main "$@"



