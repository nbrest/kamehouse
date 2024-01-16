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
  log.info "Building docker image: nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  cd ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/release/java11-release
  docker build \
    --build-arg RELEASE_VERSION="${RELEASE_VERSION}" \
    -t nbrest/kamehouse:${DOCKER_IMAGE_TAG} .
}

parseArguments() {
  while getopts ":v:" OPT; do
    case $OPT in
    ("v")
      RELEASE_VERSION="v"$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

printHelpOptions() {
  addHelpOption "-v [9.99]" "build the docker image for a release version. default is ${RELEASE_VERSION}"
}

main "$@"
