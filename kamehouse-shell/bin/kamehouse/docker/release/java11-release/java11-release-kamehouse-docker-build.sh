#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker/release/java11-release-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing java11-release-functions.sh" ; exit 99
fi

mainProcess() {
  log.info "Building docker image: nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  cd ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/release/java11-release
  docker build \
    --build-arg RELEASE_VERSION="${RELEASE_VERSION}" \
    -t nbrest/kamehouse:${DOCKER_IMAGE_TAG} .
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
      -v)
        RELEASE_VERSION="v${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-v [9.99]" "build the docker image for a release version. default is ${RELEASE_VERSION}"
}

main "$@"
