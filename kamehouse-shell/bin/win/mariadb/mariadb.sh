#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/.kamehouse/.shell/shell.pwd
LOG_PROCESS_TO_FILE=false

mainProcess() {
  log.info "mariadb -u root -p***"
  mariadb -u root -p${MARIADB_PASS_ROOT_WIN}
}

main "$@"
