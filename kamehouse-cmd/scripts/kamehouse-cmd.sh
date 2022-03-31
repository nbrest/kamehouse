#!/bin/bash

KAMEHOUSE_CMD_PATH=${HOME}/programs/kamehouse-cmd

function main() {
  executeApp "$@"
}

executeApp() {
  KAMEHOUSE_CMD_APP=`ls -1 ${KAMEHOUSE_CMD_PATH}/lib/kamehouse-cmd-*`
  java -jar ${KAMEHOUSE_CMD_APP} "$@"
}

main "$@"
