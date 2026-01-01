#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi
importFunctions ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker/release/java11-release-functions.sh

mainProcess() {
  log.info "Running docker image: nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  docker run --rm \
    --name kamehouse-${DOCKER_IMAGE_TAG} \
    -p ${DOCKER_HTTP_PORT}:80 \
    -p ${DOCKER_SSH_PORT}:22 \
    nbrest/kamehouse:${DOCKER_IMAGE_TAG}
}

main "$@"
