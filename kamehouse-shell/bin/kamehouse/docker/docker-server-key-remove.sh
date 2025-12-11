#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then echo "Error importing docker-functions.sh" ; exit 99 ; fi

mainProcess() {
  checkDockerScripsEnabled
  removeServerKey
}

removeServerKey() {
  log.info "Removing server key from known hosts"
  log.debug "ssh-keygen -f \"${HOME}/.ssh/known_hosts\" -R \"[localhost]:${DOCKER_PORT_SSH}\""
  ssh-keygen -f "${HOME}/.ssh/known_hosts" -R "[localhost]:${DOCKER_PORT_SSH}"
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
