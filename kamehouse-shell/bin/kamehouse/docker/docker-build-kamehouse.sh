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

LOG_PROCESS_TO_FILE=false

# When I update the base image here also update docker-setup.md
DOCKER_IMAGE_BASE="ubuntu:20.04"
DOCKER_IMAGE_TAG="latest"
DOCKER_ENVIRONMENT="ubuntu"

mainProcess() {
  log.info "Building docker image nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  log.debug "docker build --build-arg DOCKER_IMAGE_BASE=${DOCKER_IMAGE_BASE} -t nbrest/kamehouse:${DOCKER_IMAGE_TAG} ."
  docker build --build-arg DOCKER_IMAGE_BASE=${DOCKER_IMAGE_BASE} -t nbrest/kamehouse:${DOCKER_IMAGE_TAG} .
}

parseArguments() {
  parseDockerOs "$@"
}

setEnvFromArguments() {
  setEnvForDockerOs
}

printHelpOptions() {
  printDockerOsOption
}

main "$@"
