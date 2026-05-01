#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/kamehouse/docker-functions.sh

initScriptEnv() {
  DOCKER_DEV_PROJECT_DIR=${HOME}/workspace/kamehouse 
}

mainProcess() {
  if ${IS_DOCKER_CONTAINER}; then 
    DOCKER_DEV_PROJECT_DIR=${HOME}/git/kamehouse
  fi 
  log.info "Changing ownership of ${COL_BLUE}${DOCKER_DEV_PROJECT_DIR}${COL_DEFAULT_LOG} to ${COL_BLUE}${USER}:${USER}${COL_DEFAULT_LOG}"
  sudo chown -R ${USER}:${USER} ${DOCKER_DEV_PROJECT_DIR}
  ls -la ${DOCKER_DEV_PROJECT_DIR}
}

main "$@"
