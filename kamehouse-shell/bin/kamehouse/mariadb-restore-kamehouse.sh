#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  MARIADB_DUMP_FILE=${HOME}/.kamehouse/config/mariadb/dump/dump-kamehouse.sql
}

mainProcess() {
  if [ -f "${MARIADB_DUMP_FILE}" ]; then
    log.info "Restoring kamehouse database from ${MARIADB_DUMP_FILE}"
    mariadb -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} kamehouse < ${MARIADB_DUMP_FILE}
    checkCommandStatus "$?" "Error loading dump-kamehouse.sql"
    log.info "mariadb restore command completed successfully"
  else
    log.error "${MARIADB_DUMP_FILE} doesn't exist."
    exitProcess ${EXIT_ERROR}
  fi
}

main "$@"



