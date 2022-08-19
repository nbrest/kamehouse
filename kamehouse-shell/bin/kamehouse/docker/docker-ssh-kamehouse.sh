#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  log.info "Executing ssh into docker container with profile ${COL_PURPLE}${DOCKER_PROFILE}"
  log.warn "If I get an error that the server key changed, execute the script ${COL_RED}docker-server-key-remove.sh -p ${DOCKER_PROFILE}"
  log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost"
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost
}

parseArguments() {
  parseDockerProfile "$@"
}

setEnvFromArguments() {
  setEnvForDockerProfile
}

printHelpOptions() {
  printDockerProfileOption
}

main "$@"
