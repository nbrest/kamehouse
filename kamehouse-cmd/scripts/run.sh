#!/bin/bash
#########################################################
# These are develompment scripts meant to be used while #
# developing, testing and building the application.     #
#########################################################

main() {
  setProjectDirs "$@"
  cd ${PROJECT_DIR}

  runApp "$@"

  cd ${CURRENT_DIR}
}

runApp() {
  target/kamehouse-cmd/bin/kamehouse-cmd.sh "$@"
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