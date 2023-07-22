#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 1
fi

REMOVE_SERVER_KEY=false

mainProcess() {
  log.info "Executing ssh into docker container with profile ${COL_PURPLE}${DOCKER_PROFILE}"
  if ${REMOVE_SERVER_KEY}; then
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-server-key-remove.sh -p ${DOCKER_PROFILE}
  else
    log.warn "If I get an error that the server key changed, execute this script with ${COL_RED}-r"
  fi

  log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost"
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost
}

parseArguments() {
  parseDockerProfile "$@"
    while getopts ":p:r" OPT; do
    case $OPT in 
    ("r")
      REMOVE_SERVER_KEY=true
      ;;      
    (\?)
      parseInvalidArgument "$OPTARG"
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
