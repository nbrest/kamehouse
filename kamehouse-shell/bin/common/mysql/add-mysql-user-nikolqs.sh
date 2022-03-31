#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

source ${HOME}/.kamehouse/.shell/.cred
LOG_PROCESS_TO_FILE=false

mainProcess() {
  log.info "Adding user nikolqs to mysql db"
  log.info "This script needs to be executed as root and root must have \${HOME}/programs and \${HOME}/.kamehouse symlinks"

  mysql -v -v -v -e "DROP USER IF EXISTS nikolqs;"

  log.info "executing CREATE USER nikolqs"
  mysql -e "CREATE USER nikolqs@'%' identified by '${MYSQL_PASS_NIKOLQS}';"

  mysql -v -v -v -e "GRANT ALL PRIVILEGES ON *.* TO 'nikolqs'@'%';"
}

main "$@"
