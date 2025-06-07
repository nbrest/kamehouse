#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then echo "Error importing docker-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  CONTAINER=""
}

mainProcess() {
  if [ -z "${CONTAINER}" ]; then 
    log.info "Container not passed as argument, attempting to find a running kamehouse container of profile ${COL_PURPLE}${DOCKER_PROFILE}"
    CONTAINER=`docker container list | grep -e "kamehouse\|/bin/sh -c" | grep "${DOCKER_PORT_SSH}" |  cut -d ' ' -f1`
  fi

  if [ -n "${CONTAINER}" ]; then 
    log.info "Stopping container ${COL_PURPLE}${CONTAINER}"
    log.debug "docker stop ${CONTAINER}"
    docker stop ${CONTAINER}
  else
    log.warn "No kamehouse container running detected for profile ${COL_PURPLE}${DOCKER_PROFILE}"
  fi

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-status-kamehouse.sh
}

parseArguments() {
  parseDockerProfile "$@"

  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -p)
        # parsed in a previous parse options function 
        ;;
      -c)
        CONTAINER="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  setEnvForDockerProfile
}

printHelpOptions() {
  addHelpOption "-c (container id)" "id of the container to stop"
  printDockerProfileOption
}

main "$@"
