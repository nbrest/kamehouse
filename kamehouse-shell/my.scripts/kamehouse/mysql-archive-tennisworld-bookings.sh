#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

source ${HOME}/my.scripts/.cred/.cred

# Global variables
LOG_PROCESS_TO_FILE=true
MYSQL_ARCHIVE_FILE=${HOME}/my.scripts/kamehouse/sql/mysql/archive-tennisworld-bookings.sql

mainProcess() {
  log.info "Archiving old tennis world bookings"
  mysql -u nikolqs -p${MYSQL_PASS_NIKOLQS} kameHouse < ${MYSQL_ARCHIVE_FILE}
  checkCommandStatus "$?"
}

main "$@"



