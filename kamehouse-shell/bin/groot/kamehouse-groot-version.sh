#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

mainProcess() {
  local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
  local GROOT_VERSION_FILE="${HTTPD_CONTENT_ROOT}/kame-house-groot/build-info.json"
  cat "${GROOT_VERSION_FILE}"
}

main "$@"
