#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  PATH_DUMP_FILE=${HOME}/.kamehouse/backup/db/dump
  SCHEMAS=(kamehouse kamehouse_tennisworld kamehouse_ztvrc kamehouse_testmodule)
  DUMP_FILENAMES=(dump-kamehouse.sql dump-kamehouse-tennisworld.sql dump-kamehouse-ztvrc.sql dump-kamehouse-testmodule.sql)
}

mainProcess() {
  for i in "${!SCHEMAS[@]}"; do
    SCHEMA=${SCHEMAS[i]}
    MARIADB_DUMP_FILE=${PATH_DUMP_FILE}/${DUMP_FILENAMES[i]}
    if [ -f "${MARIADB_DUMP_FILE}" ]; then
      log.info "Restoring ${SCHEMA} from ${MARIADB_DUMP_FILE}"
      mariadb -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} ${SCHEMA} < ${MARIADB_DUMP_FILE}
      checkCommandStatus "$?" "Error restoring ${SCHEMA} from ${DUMP_FILENAMES[i]}"
    elif [ "${i}" -eq 0 ]; then
      log.error "${MARIADB_DUMP_FILE} doesn't exist."
      exitProcess ${EXIT_ERROR}
    else
      log.warn "${MARIADB_DUMP_FILE} doesn't exist. Skipping ${SCHEMA}."
    fi
  done
  log.info "mariadb restore commands completed successfully"
}

main "$@"
