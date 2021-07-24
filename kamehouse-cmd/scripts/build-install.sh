#!/bin/bash
#########################################################
# These are develompment scripts meant to be used while #
# developing, testing and building the application.     #
#########################################################
# parameters: maven options

main() {
  setProjectDirs "$@"
  cd ${PROJECT_DIR}

  scripts/build.sh "$@"
  install

  cd ${CURRENT_DIR}
}

install() {
  unzip target/kamehouse-cmd-bundle.zip -d target/ 
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

