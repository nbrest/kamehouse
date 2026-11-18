#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

initScriptEnv() {
  ALLOW_PARALLEL_RUNS=true
}

mainProcess() {
  local KAMEHOUSE_SHELL_PATH=${HOME}/.kamehouse/opt/kamehouse-shell
  local KAMEHOUSE_SHELL_CONF_PATH=${KAMEHOUSE_SHELL_PATH}/conf
  local SHELL_VERSION_FILE="${KAMEHOUSE_SHELL_CONF_PATH}/build-info.json"
  cat "${SHELL_VERSION_FILE}"
}

main "$@"
