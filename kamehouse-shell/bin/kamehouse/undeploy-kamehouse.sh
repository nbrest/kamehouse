#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 9
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 9
fi
source ${HOME}/.kamehouse/.shell/.cred

# Initial config
LOG_PROCESS_TO_FILE=true
OPERATION=undeploy

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
