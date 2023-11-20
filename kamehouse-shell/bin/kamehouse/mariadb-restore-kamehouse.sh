#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

source ${HOME}/.kamehouse/.shell/.cred

LOG_PROCESS_TO_FILE=true
MARIADB_DUMP_FILE=${HOME}/home-synced/mariadb/dump/dump-kamehouse.sql

mainProcess() {
  if [ -f "${MARIADB_DUMP_FILE}" ]; then
    log.info "Restoring kamehouse database from ${MARIADB_DUMP_FILE}"
    mariadb -u nikolqs -p${MARIADB_PASS_NIKOLQS} kameHouse < ${MARIADB_DUMP_FILE}
    checkCommandStatus "$?"
    log.info "mariadb restore command completed successfully"
  else
    log.error "${MARIADB_DUMP_FILE} doesn't exist."
    exitProcess 1
  fi
}

main "$@"


