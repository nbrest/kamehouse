#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  # Run the collection from the current directory, which must be the kamehouse repo root
  USE_CURRENT_DIR=true
  PROFILE="dev-tomcat"
  POSTMAN_COLLECTION="postman/collections/kame-house.postman_collection.json"
  POSTMAN_ENVIRONMENT=""
  POSTMAN_GLOBALS="postman/globals/workspace.postman_globals.json"
}

mainProcess() {
  setKameHouseRootProjectDir
  setPostmanEnvironment
  log.info "Running the KameHouse Postman collection against profile ${COL_PURPLE}${PROFILE}"
  local LOCAL_NEWMAN="${PROJECT_DIR}/postman/node_modules/.bin/newman"
  local NEWMAN_CMD=(npx --yes newman)
  if [ -x "${LOCAL_NEWMAN}" ]; then
    NEWMAN_CMD=("${LOCAL_NEWMAN}")
  fi
  "${NEWMAN_CMD[@]}" run "${POSTMAN_COLLECTION}" \
    -e "${POSTMAN_ENVIRONMENT}" \
    -g "${POSTMAN_GLOBALS}" \
    --ignore-redirects \
    --bail \
    --reporters cli \
    --timeout-request 60000 \
    --color off
  checkCommandStatus "$?" "Error running the KameHouse Postman collection"
}

setPostmanEnvironment() {
  case "${PROFILE}" in
    dev-tomcat)
      POSTMAN_ENVIRONMENT="postman/environments/localhost-9989-dev-tomcat.postman_environment.json"
      ;;
    dev-docker)
      POSTMAN_ENVIRONMENT="postman/environments/localhost-6080-dev-docker.postman_environment.json"
      ;;
    prod-https)
      POSTMAN_ENVIRONMENT="postman/environments/localhost-443-prod-httpd.postman_environment.json"
      ;;
    ci-docker)
      POSTMAN_ENVIRONMENT="postman/environments/localhost-15080-ci-docker.postman_environment.json"
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
  addHelpOption "-p --profile [dev-tomcat|dev-docker|prod-https|ci-docker]" "environment to run the collection against. Default is dev-tomcat"
}

main "$@"
