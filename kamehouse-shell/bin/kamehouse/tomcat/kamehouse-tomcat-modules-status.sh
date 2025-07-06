#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

mainProcess() {
  log.debug "curl http://${TOMCAT_TEXT_USER}:****@localhost:${TOMCAT_PORT}/manager/text/list 2>/dev/null | sort"
  curl http://${TOMCAT_TEXT_USER}:${TOMCAT_TEXT_PASS}@localhost:${TOMCAT_PORT}/manager/text/list 2>/dev/null | sort
}

parseArguments() {
  parseTomcatPort "$@"
}

setEnvFromArguments() {
  setEnvForTomcatPort
}

printHelpOptions() {
  printTomcatPortOption
}

main "$@"
