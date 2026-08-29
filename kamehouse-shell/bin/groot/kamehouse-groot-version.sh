#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/kamehouse/kamehouse-module-version-functions.sh

initKameHouseShellEnv() {
  LOG=DISABLED
}

mainProcess() {
  local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
  if ${IS_DEV_ENVIRONMENT}; then
    HTTPD_CONTENT_ROOT="${HTTPD_CONTENT_ROOT}-dev"
  fi
  local GROOT_VERSION_FILE="${HTTPD_CONTENT_ROOT}/kame-house-groot/build-info.json"
  cat "${GROOT_VERSION_FILE}"
}

main "$@"
