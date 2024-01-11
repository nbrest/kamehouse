#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker/release/java8-release-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing java8-release-functions.sh\033[0;39m"
  exit 99
fi

REMOVE_SERVER_KEY=false
FIRST_RELEASE_FLAG=""

mainProcess() {
  if ${REMOVE_SERVER_KEY}; then
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/release/java8-release/java8-release-kamehouse-docker-server-key-remove.sh ${FIRST_RELEASE_FLAG}
  else
    log.warn "If I get an error that the server key changed, execute this script with ${COL_RED}java8-release-kamehouse-docker-server-key-remove.sh -r ${FIRST_RELEASE_FLAG}"
  fi  
  log.info "Executing ssh into docker container kamehouse-${DOCKER_IMAGE_TAG}"
  ssh -p ${DOCKER_SSH_PORT} ${DOCKER_CONTAINER_USERNAME}@localhost
}

setEnvForFirstRelease() {
  if ${FIRST_RELEASE}; then
    DOCKER_SSH_PORT=${FIRST_RELEASE_SSH_PORT}
    DOCKER_IMAGE_TAG=${FIRST_RELEASE_IMAGE_TAG}
  fi
}

parseArguments() {
  while getopts ":fr" OPT; do
    case $OPT in
    ("f")
      FIRST_RELEASE=true
      FIRST_RELEASE_FLAG="-f"
      ;;
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
  setEnvForFirstRelease
}

printHelpOptions() {
  addHelpOption "-f" "ssh into the docker image for the first release version"
  addHelpOption "-r" "remove server key from known hosts. Use when docker container ssh keys change"
}

main "$@"
