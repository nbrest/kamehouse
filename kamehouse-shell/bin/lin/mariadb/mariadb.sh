#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99
fi

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
}

mainProcess() {
  sudo mariadb
}

main "$@"
