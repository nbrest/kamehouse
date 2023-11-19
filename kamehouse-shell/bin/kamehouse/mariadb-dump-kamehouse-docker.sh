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
  checkIfContainerIsRunning
  exportMariadbDataOnDocker
  copyDataFromContainerToHost
}

checkIfContainerIsRunning() {
  log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'ls' > /dev/null"
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'ls' > /dev/null
  if [ "$?" != "0" ]; then
    log.error "Can't connect to container. Exiting process"
    exit 1
  fi
}

exportMariadbDataOnDocker() {
	log.info "Exporting mariadb data from mariadb server on docker container"
  log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb-csv-kamehouse.sh\""
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb-csv-kamehouse.sh"
  
  log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb-dump-kamehouse.sh\""
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb-dump-kamehouse.sh"
}

copyDataFromContainerToHost() {
	log.info "Exporting data from container to host"
  mkdir -p ${HOME}/home-synced/docker/mariadb
  rm -rf ${HOME}/home-synced/docker/mariadb
  log.debug "scp -C -r -P ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/mariadb ${HOME}/home-synced/docker/mariadb"
  scp -C -r -P ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/mariadb ${HOME}/home-synced/docker/mariadb
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
