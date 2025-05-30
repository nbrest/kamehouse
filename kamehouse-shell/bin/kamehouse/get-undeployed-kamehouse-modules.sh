#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

initScriptEnv() {
  UNDEPLOYED_MODULES=""
}

mainProcess() {
  if ! ${DEPLOY_KAMEHOUSE_TOMCAT_MODULES}; then
    UNDEPLOYED_MODULES="${TOMCAT_MODULES},"
  fi
  
  if ! ${DEPLOY_KAMEHOUSE_CMD}; then
    UNDEPLOYED_MODULES="${UNDEPLOYED_MODULES}cmd,"
  fi
  
  if ! ${DEPLOY_KAMEHOUSE_SHELL}; then
    UNDEPLOYED_MODULES="${UNDEPLOYED_MODULES}shell,"
  fi
  
  if ! ${DEPLOY_KAMEHOUSE_GROOT}; then
    UNDEPLOYED_MODULES="${UNDEPLOYED_MODULES}groot,"  
  fi

  if [ -n "${UNDEPLOYED_MODULES}" ]; then
    UNDEPLOYED_MODULES="${UNDEPLOYED_MODULES::-1}"
  fi
  echo "undeployedKameHouseModules=${UNDEPLOYED_MODULES}"
}

main "$@"
