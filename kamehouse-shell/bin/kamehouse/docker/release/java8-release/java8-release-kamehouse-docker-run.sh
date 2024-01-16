#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker/release/java8-release-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing java8-release-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  log.info "Running docker image: nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  docker run --rm \
    --name kamehouse-${DOCKER_IMAGE_TAG} \
    -p ${DOCKER_HTTP_PORT}:80 \
    -p ${DOCKER_SSH_PORT}:22 \
    nbrest/kamehouse:${DOCKER_IMAGE_TAG}
}

setEnvForFirstRelease() {
  if ${FIRST_RELEASE}; then
    DOCKER_SSH_PORT=${FIRST_RELEASE_SSH_PORT}
    DOCKER_HTTP_PORT=${FIRST_RELEASE_HTTP_PORT}
    DOCKER_IMAGE_TAG=${FIRST_RELEASE_IMAGE_TAG}
  fi
}

parseArguments() {
  while getopts ":f" OPT; do
    case $OPT in
    ("f")
      FIRST_RELEASE=true
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
  addHelpOption "-f" "run the docker image the first release version"
}

main "$@"
