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

mainProcess() {
  log.info "Pushing docker image nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  docker push nbrest/kamehouse:${DOCKER_IMAGE_TAG}
}

setEnvForFirstRelease() {
  if ${FIRST_RELEASE}; then
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
  addHelpOption "-f" "push the docker image for the first release version"
}

main "$@"
