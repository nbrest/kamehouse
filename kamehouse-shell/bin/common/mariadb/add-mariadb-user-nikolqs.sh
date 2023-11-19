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

source ${HOME}/.kamehouse/.shell/.cred
LOG_PROCESS_TO_FILE=true
PATH_SQL=${HOME}/programs/kamehouse-shell/bin/kamehouse/sql/mariadb

mainProcess() {
  log.info "Adding user nikolqs to mariadb"
  setSudoKameHouseCommand "mariadb"
  ${SUDO_KAMEHOUSE_COMMAND} -e"set @nikoLqsPass = '${MARIADB_PASS_NIKOLQS}'; `cat ${PATH_SQL}/add-mariadb-user-nikolqs.sql`"
}

main "$@"
