#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  # Run the collection from the current directory, which must be the kamehouse repo root
  USE_CURRENT_DIR=true
  HTTP_DIR="http"
  PROFILE="dev-tomcat"
  RUN_DESTRUCTIVE_TESTS=false
}

mainProcess() {
  setKameHouseRootProjectDir
  validateProfile
  log.info "Running the KameHouse .http collection against profile ${COL_PURPLE}${PROFILE}"
  cd "${HTTP_DIR}"
  checkCommandStatus "$?" "Error changing to directory ${HTTP_DIR}"
  npm run "send:${PROFILE}" -- --bail --no-color --var RUN_DESTRUCTIVE_TESTS="${RUN_DESTRUCTIVE_TESTS}"
  checkCommandStatus "$?" "Error running the KameHouse .http collection"
}

validateProfile() {
  case "${PROFILE}" in
    dev-tomcat|dev-docker|prod-https|ci-docker)
      ;;
    *)
      log.error "Option -p ${PROFILE} has an invalid value"
      printHelp
      exitProcess ${EXIT_INVALID_ARG}
      ;;
  esac
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -d|--destructive)
        RUN_DESTRUCTIVE_TESTS=true
        ;;
      -p|--profile)
        PROFILE="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;
    esac
  done
}

printHelpOptions() {
  addHelpOption "-d --destructive" "also run the reboot and shutdown requests"
  addHelpOption "-p --profile [dev-tomcat|dev-docker|prod-https|ci-docker]" "environment to run the collection against. Default is dev-tomcat"
}

main "$@"
