#!/bin/bash

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
