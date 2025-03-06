#!/bin/bash

# Disable logs
LOG=ERROR
SKIP_LOG_START_FINISH=true

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
  local GROOT_VERSION_FILE="${HTTPD_CONTENT_ROOT}/kame-house-groot/groot-version.txt"
  cat "${GROOT_VERSION_FILE}"
}

main "$@"
