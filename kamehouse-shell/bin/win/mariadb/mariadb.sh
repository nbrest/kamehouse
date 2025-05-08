#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
  LOAD_KAMEHOUSE_SECRETS=true
}

mainProcess() {
  log.info "mariadb -u root -p***"
  mariadb -u root -p${MARIADB_PASS_ROOT_WIN}
}

main "$@"
