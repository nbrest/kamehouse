#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  # Run the collection from the current directory, which must be the kamehouse repo root
  USE_CURRENT_DIR=true
  HTTP_DIR="http"
  RUN_DESTRUCTIVE_TESTS=false
}

mainProcess() {
  setKameHouseRootProjectDir
  log.info "Running the KameHouse .http collection against the local dev environment"
  cd "${HTTP_DIR}"
  checkCommandStatus "$?" "Error changing to directory ${HTTP_DIR}"
  npm run send:dev-tomcat -- --bail --no-color --var RUN_DESTRUCTIVE_TESTS="${RUN_DESTRUCTIVE_TESTS}"
  checkCommandStatus "$?" "Error running the KameHouse .http collection"
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    case "${CURRENT_OPTION}" in
      -d|--destructive)
        RUN_DESTRUCTIVE_TESTS=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;
    esac
  done
}

printHelpOptions() {
  addHelpOption "-d --destructive" "also run the reboot and shutdown requests"
}

main "$@"
