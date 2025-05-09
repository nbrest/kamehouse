#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  OPERATION=undeploy
}

mainProcess() {
  executeOperationInTomcatManager ${OPERATION} ${TOMCAT_PORT} ${MODULE_SHORT}
}

parseArguments() {
  parseKameHouseModule "$@"
  parseTomcatPort "$@"
}

setEnvFromArguments() {
  setEnvForKameHouseModule
  setEnvForTomcatPort
}

printHelpOptions() {
  printKameHouseModuleOption "${OPERATION}"
  printTomcatPortOption
}

main "$@"
