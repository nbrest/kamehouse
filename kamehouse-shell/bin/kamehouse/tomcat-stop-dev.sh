#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
  LOG_PROCESS_TO_FILE=false
}

mainProcess() {
  if ${IS_LINUX_HOST}; then
    ${HOME}/programs/kamehouse-shell/bin/lin/kamehouse/tomcat-stop.sh -p ${DEFAULT_TOMCAT_DEV_PORT}
  else
    ${HOME}/programs/kamehouse-shell/bin/win/kamehouse/tomcat-stop.sh -p ${DEFAULT_TOMCAT_DEV_PORT}
  fi
}

main "$@"
