#!/bin/bash

if (( $EUID == 0 )); then
  HOME="/var/www"
fi

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=true
PATH_SQL=${HOME}/programs/kamehouse-shell/bin/kamehouse/sql/mariadb

mainProcess() {
  log.info "Setting up kamehouse database"
  log.info "Executing setup-kamehouse.sql"
  setSudoKameHouseCommand "mariadb"
  ${SUDO_KAMEHOUSE_COMMAND} -v < ${PATH_SQL}/setup-kamehouse.sql
  checkCommandStatus "$?" "Error running setup-kamehouse.sql"

  log.info "Executing spring-session.sql"
  ${SUDO_KAMEHOUSE_COMMAND} kameHouse < ${PATH_SQL}/spring-session.sql 
  checkCommandStatus "$?" "Error running spring-session.sql"
  log.info "Finished setting up kamehouse database"
}

main "$@"



