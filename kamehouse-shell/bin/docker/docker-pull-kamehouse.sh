#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/docker/docker-functions.sh

initScriptConfig() {
  DOCKER_PULL_TIMEOUT=900
}

mainProcess() {
  checkDockerScripsEnabled
  log.info "Pulling docker image nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  log.debug "docker pull nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  execWithTimeout ${DOCKER_PULL_TIMEOUT} docker pull nbrest/kamehouse:${DOCKER_IMAGE_TAG}

  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/docker/docker-cleanup-kamehouse.sh
}

parseArguments() {
  parseDockerTag "$@"
}

setEnvFromArguments() {
  setEnvForDockerTag 
}

printHelpOptions() {
  printDockerTagOption
}

main "$@"
