#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker/release/java11-release-functions.sh
if [ "$?" != "0" ]; then echo "Error importing java11-release-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  REMOVE_SERVER_KEY=false
}

mainProcess() {
  if ${REMOVE_SERVER_KEY}; then
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/release/java11-release/java11-release-kamehouse-docker-server-key-remove.sh
  else
    log.warn "If I get an error that the server key changed, execute this script with ${COL_RED}java11-release-kamehouse-docker-server-key-remove.sh -r"
  fi  
  log.info "Executing ssh into docker container kamehouse-${DOCKER_IMAGE_TAG}"
  ssh -p ${DOCKER_SSH_PORT} ${DOCKER_CONTAINER_USERNAME}@localhost
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
      -r)
        REMOVE_SERVER_KEY=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-r" "remove server key from known hosts. Use when docker container ssh keys change"
}

main "$@"
