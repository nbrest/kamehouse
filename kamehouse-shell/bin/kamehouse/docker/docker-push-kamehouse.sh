#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing docker-functions.sh" ; exit 99
fi

mainProcess() {
  log.info "Pushing docker image nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  log.debug "docker push nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  docker push nbrest/kamehouse:${DOCKER_IMAGE_TAG}
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
