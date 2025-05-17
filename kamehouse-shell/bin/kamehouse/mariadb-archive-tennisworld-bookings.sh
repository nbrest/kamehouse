#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  MARIADB_ARCHIVE_FILE=${HOME}/programs/kamehouse-shell/sql/mariadb/archive-tennisworld-bookings.sql
}

mainProcess() {
  log.info "Archiving old tennis world bookings"
  mariadb -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} kamehouse < ${MARIADB_ARCHIVE_FILE}
  checkCommandStatus "$?"
}

main "$@"



