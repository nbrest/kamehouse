#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi
importFunctions ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker/release/java8-release-functions.sh

mainProcess() {
  log.info "Removing server key from known hosts"
  ssh-keygen -f "${HOME}/.ssh/known_hosts" -R "[localhost]:${DOCKER_SSH_PORT}"
}

setEnvForFirstRelease() {
  if ${FIRST_RELEASE}; then
    DOCKER_SSH_PORT=${FIRST_RELEASE_SSH_PORT}
  fi
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
      -f)
        FIRST_RELEASE=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  setEnvForFirstRelease
}

printHelpOptions() {
  addHelpOption "-f" "clear server keys on the docker image for the first release version"
}

main "$@"
