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
  local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
  local UI_VERSION_FILE="${HTTPD_CONTENT_ROOT}/kame-house/build-info.json"
  cat "${UI_VERSION_FILE}"
}

main "$@"
