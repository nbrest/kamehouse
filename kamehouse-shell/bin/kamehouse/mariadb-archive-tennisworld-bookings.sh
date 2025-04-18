#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi
loadKamehouseSecrets

# Global variables
MARIADB_ARCHIVE_FILE=${HOME}/programs/kamehouse-shell/sql/mariadb/archive-tennisworld-bookings.sql

mainProcess() {
  log.info "Archiving old tennis world bookings"
  mariadb -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} kamehouse < ${MARIADB_ARCHIVE_FILE}
  checkCommandStatus "$?"
}

main "$@"



