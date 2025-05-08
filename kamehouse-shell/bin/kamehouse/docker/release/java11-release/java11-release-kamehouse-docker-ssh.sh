#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker/release/java11-release-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing java11-release-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  if ${REMOVE_SERVER_KEY}; then
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/release/java11-release/java11-release-kamehouse-docker-server-key-remove.sh
  else
    log.warn "If I get an error that the server key changed, execute this script with ${COL_RED}java11-release-kamehouse-docker-server-key-remove.sh -r"
  fi  
  log.info "Executing ssh into docker container kamehouse-${DOCKER_IMAGE_TAG}"
  ssh -p ${DOCKER_SSH_PORT} ${DOCKER_CONTAINER_USERNAME}@localhost
}

initScriptEnv() {
  REMOVE_SERVER_KEY=false
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
