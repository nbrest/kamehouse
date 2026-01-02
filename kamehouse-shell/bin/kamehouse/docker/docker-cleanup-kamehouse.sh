#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/kamehouse/docker-functions.sh

mainProcess() {
  checkDockerScripsEnabled
  listDockerImages
  removeUntaggedImages
  listDockerImages
}

listDockerImages() {
  log.info "Docker images: "
  docker image list
}

removeUntaggedImages() {
  log.info "Removing untagged kamehouse docker images"

  local DOCKER_IMAGES=`docker image list | grep "<none>" | awk '{print $3}'`
  echo -e "${DOCKER_IMAGES}" | while read DOCKER_IMAGE; do
    if [ -n "${DOCKER_IMAGE}" ] && [ "${DOCKER_IMAGE}" != "" ]; then
      log.info "Removing image ${COL_PURPLE}${DOCKER_IMAGE}"
      docker image rm ${DOCKER_IMAGE}
    fi
  done
}

main "$@"
