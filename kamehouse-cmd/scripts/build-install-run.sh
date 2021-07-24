#!/bin/bash
#########################################################
# These are develompment scripts meant to be used while #
# developing, testing and building the application.     #
#########################################################
# parameters: maven options

# This script builds with compiler parameters and runs the application without runtime parameters.
# To compile and run the application with parameters, use the script build-install-run-params.sh

main() {
  setProjectDirs "$@"
  cd ${PROJECT_DIR}

  scripts/build-install.sh "$@"
  scripts/run.sh

  cd ${CURRENT_DIR}
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

