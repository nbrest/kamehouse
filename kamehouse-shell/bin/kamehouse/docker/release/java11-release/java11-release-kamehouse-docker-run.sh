#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 149
fi

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker/release/java11-release-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing java11-release-functions.sh\033[0;39m"
  exit 149
fi

mainProcess() {
  log.info "Running docker image: nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  docker run --rm \
    --name kamehouse-${DOCKER_IMAGE_TAG} \
    -p ${DOCKER_HTTP_PORT}:80 \
    -p ${DOCKER_SSH_PORT}:22 \
    nbrest/kamehouse:${DOCKER_IMAGE_TAG}
}

main "$@"
