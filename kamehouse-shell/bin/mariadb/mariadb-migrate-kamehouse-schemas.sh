#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  return
}

initScriptEnv() {
  PATH_SQL=${HOME}/.kamehouse/opt/kamehouse-shell/sql/mariadb
  MARIADB_MIGRATION_FILE=${PATH_SQL}/migrate-kamehouse-schemas.sql
}

mainProcess() {
  log.info "Migrating kamehouse domain tables into separate schemas"
  setSudoKameHouseCommand "mariadb"
  ${SUDO_KAMEHOUSE_COMMAND} -v < ${MARIADB_MIGRATION_FILE}
  checkCommandStatus "$?" "Error running migrate-kamehouse-schemas.sql"
  log.info "Schema migration completed. Old kamehouse tables were left untouched"
}

mainProcessPost() {
  log.info "Finished migrating kamehouse schemas"
}

main "$@"
