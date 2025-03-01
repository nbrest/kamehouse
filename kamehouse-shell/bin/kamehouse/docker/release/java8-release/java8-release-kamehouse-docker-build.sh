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
      -v)
        RELEASE_VERSION="v${CURRENT_OPTION_ARG}"
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
  addHelpOption "-f" "build the docker image the first release version ${FIRST_RELEASE_VERSION}"
  addHelpOption "-v [9.99]" "build the docker image for a release version. default is ${RELEASE_VERSION}"
}

main "$@"
