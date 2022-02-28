#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  CONTAINER=$1

  if [ -z "${CONTAINER}" ]; then 
    log.info "Container not passed as argument, attempting to find a running kamehouse container"
    CONTAINER=`docker container list | grep -e "java.web.kamehouse\\|\\/home\\/nbrest\\/docker" |  cut -d ' ' -f1`
  fi

  if [ -n "${CONTAINER}" ]; then 
    log.info "Stopping container ${COL_PURPLE}${CONTAINER}"
    docker stop ${CONTAINER}
  else
    log.warn "No kamehouse container running detected"
  fi

  docker-status-java-web-kamehouse.sh
}

main "$@"