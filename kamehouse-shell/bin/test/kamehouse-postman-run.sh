#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  # Run the collection from the current directory, which must be the kamehouse repo root
  USE_CURRENT_DIR=true
  POSTMAN_COLLECTION="postman/collections/kame-house.postman_collection.json"
  POSTMAN_ENVIRONMENT="postman/environments/localhost-9989-dev-tomcat.postman_environment.json"
  POSTMAN_GLOBALS="postman/globals/workspace.postman_globals.json"
}

mainProcess() {
  setKameHouseRootProjectDir
  log.info "Running the KameHouse Postman collection against the local dev environment"
  npx --yes newman run "${POSTMAN_COLLECTION}" \
    -e "${POSTMAN_ENVIRONMENT}" \
    -g "${POSTMAN_GLOBALS}" \
    --ignore-redirects \
    --bail \
    --reporters cli \
    --timeout-request 60000 \
    --color off
  checkCommandStatus "$?" "Error running the KameHouse Postman collection"
}

main "$@"
