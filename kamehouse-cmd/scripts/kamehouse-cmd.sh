#!/bin/bash

KAMEHOUSE_CMD_PATH=${HOME}/programs/kamehouse-cmd

main() {
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
