#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 99
fi

LOG_PROCESS_TO_FILE=true

mainProcess() {
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
