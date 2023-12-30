#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 149
fi

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker/release/java8-release-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing java8-release-functions.sh\033[0;39m"
  exit 149
fi

mainProcess() {
  log.info "Building docker image: nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  cd ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/release/java8-release
  docker build \
    --build-arg RELEASE_VERSION="${RELEASE_VERSION}" \
    -t nbrest/kamehouse:${DOCKER_IMAGE_TAG} .
}

setEnvForFirstRelease() {
  if ${FIRST_RELEASE}; then
    RELEASE_VERSION=${FIRST_RELEASE_VERSION}
    DOCKER_IMAGE_TAG=${FIRST_RELEASE_IMAGE_TAG}
  fi
}

parseArguments() {
  while getopts ":fv:" OPT; do
    case $OPT in
    ("f")
      FIRST_RELEASE=true
      ;;
    ("v")
      RELEASE_VERSION="v"$OPTARG
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
  addHelpOption "-f" "build the docker image the first release version ${FIRST_RELEASE_VERSION}"
  addHelpOption "-v [9.99]" "build the docker image for a release version. default is ${RELEASE_VERSION}"
}

main "$@"
