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
  exportMariadbDataOnDocker
  copyDataFromContainerToHost
}

exportMariadbDataOnDocker() {
	log.info "Exporting mariadb data from mariadb server on docker container"
  SSH_PORT="${DOCKER_PORT_SSH}"
  SSH_USER="${DOCKER_USERNAME}"
  SSH_SERVER="localhost"
  SSH_COMMAND="/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb-csv-kamehouse.sh"
  IS_REMOTE_LINUX_HOST=true
  executeSshCommand --skip-exit-code-check

  SSH_PORT="${DOCKER_PORT_SSH}"
  SSH_USER="${DOCKER_USERNAME}"
  SSH_SERVER="localhost"
  SSH_COMMAND="/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb-dump-kamehouse.sh"
  IS_REMOTE_LINUX_HOST=true
  executeSshCommand --skip-exit-code-check
}

copyDataFromContainerToHost() {
	log.info "Exporting data from container to host"
  mkdir -p ${HOME}/.kamehouse/config/docker/
  SCP_OPTIONS="-v -C -r -P ${DOCKER_PORT_SSH}"
  SCP_SRC="${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/config/mariadb"
  SCP_DEST="${HOME}/.kamehouse/config/docker/mariadb"
  executeScpCommand --skip-exit-code-check
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
