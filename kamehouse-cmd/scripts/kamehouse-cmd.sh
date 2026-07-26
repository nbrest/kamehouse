#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
  LOG_PROCESS_TO_FILE=false
}

initScriptEnv() {
  KAMEHOUSE_CMD_PATH=${HOME}/programs/kamehouse-cmd
}

mainProcessPre() {
  setEnvironment
  executeApp "$@"
}

setEnvironment() {
  if [ -z "${DISPLAY}" ]; then
    export DISPLAY=:0.0
  fi
  if [ -z "${XDG_RUNTIME_DIR}" ]; then
    export XDG_RUNTIME_DIR=/run/user/$(id -u)
  fi
}

executeApp() {
  KAMEHOUSE_CMD_APP=`ls -1 ${KAMEHOUSE_CMD_PATH}/lib/kamehouse-cmd-*`
  if [ -n "${JAVA_HOME}" ]; then
    "${JAVA_HOME}/bin/java" -jar ${KAMEHOUSE_CMD_APP} "$@"
  else
    java -jar ${KAMEHOUSE_CMD_APP} "$@"
  fi
}

main "$@"
