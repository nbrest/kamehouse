#!/bin/bash

function main() {
  setProjectDirs "$@"
  cd ${PROJECT_DIR}
  
  executeApp "$@"

  cd ${CURRENT_DIR}
}

executeApp() {
  KAMEHOUSE_CMD_APP=`ls -1 lib/kamehouse-cmd-*`
  java -jar ${KAMEHOUSE_CMD_APP} "$@"
}

setProjectDirs() {
  export CURRENT_DIR=$(pwd)
  if [[ $0 == /* ]]; then 
    export SCRIPT_DIR=$(dirname $0)
  else 
    export SCRIPT_DIR=$(dirname $(echo $(pwd)/$0))
  fi
  export PROJECT_DIR=${SCRIPT_DIR}/..
}

main "$@"