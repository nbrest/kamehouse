#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 99
fi

LOG_PROCESS_TO_FILE=false

mainProcess() {
  log.info "Executing ssh into docker container with profile ${COL_PURPLE}${DOCKER_PROFILE}"
  if ${REMOVE_SERVER_KEY}; then
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-server-key-remove.sh -p ${DOCKER_PROFILE}
  else
    log.warn "If I get an error that the server key changed, execute this script with ${COL_RED}docker-ssh-kamehouse.sh -p ${DOCKER_PROFILE} -r"
  fi

  log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost"
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost
}

setInitialGlobalEnv() {
  REMOVE_SERVER_KEY=false
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
      -r)
        REMOVE_SERVER_KEY=true
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
  printDockerProfileOption
  addHelpOption "-r" "remove server key from known hosts. Use when docker container ssh keys change"
}

main "$@"
