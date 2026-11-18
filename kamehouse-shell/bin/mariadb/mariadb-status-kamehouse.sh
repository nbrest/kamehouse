#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  ALLOW_PARALLEL_RUNS=true
  SQL_FILE=${HOME}/.kamehouse/opt/kamehouse-shell/sql/mariadb/status-kamehouse.sql
}

mainProcess() {
  log.info "KameHouse database status"
  mariadb --force --table -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} kamehouse < ${SQL_FILE}
}

main "$@"



