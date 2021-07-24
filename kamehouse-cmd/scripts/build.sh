#!/bin/bash
#########################################################
# These are develompment scripts meant to be used while #
# developing, testing and building the application.     #
#########################################################
# parameters: maven options

main() {
  setProjectDirs "$@"
  cd ${PROJECT_DIR}

  buildApp "$@"

  cd ${CURRENT_DIR}
}

buildApp() {
  MAVEN_OPTIONS="$@"
  echo "Compiling the project with the parameters: ${MAVEN_OPTIONS}"
  mvn clean package ${MAVEN_OPTIONS}
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
