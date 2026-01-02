#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/kamehouse/docker-functions.sh

mainProcess() {
  exportMariadbDataOnDocker
  copyDataFromContainerToHost
}

exportMariadbDataOnDocker() {
	log.info "Exporting mariadb data from mariadb server on docker container"
  SSH_PORT="${DOCKER_PORT_SSH}"
  SSH_USER="${DOCKER_USERNAME}"
  SSH_SERVER="localhost"
  SSH_COMMAND="/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb/mariadb-csv-kamehouse.sh"
  IS_REMOTE_LINUX_HOST=true
  executeSshCommand --skip-exit-code-check

  SSH_PORT="${DOCKER_PORT_SSH}"
  SSH_USER="${DOCKER_USERNAME}"
  SSH_SERVER="localhost"
  SSH_COMMAND="/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb/mariadb-dump-kamehouse.sh"
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
