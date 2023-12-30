#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 149
fi
# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 149
fi

source ${HOME}/.kamehouse/.shell/.cred

LOG_PROCESS_TO_FILE=true
SQL_FILE=${HOME}/programs/kamehouse-shell/bin/kamehouse/sql/mariadb/status-kamehouse.sql

mainProcess() {
  log.info "KameHouse database status"
  mariadb --force --table -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} kamehouse < ${SQL_FILE}
}

main "$@"



