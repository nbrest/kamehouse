#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker/release/java11-release-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing java11-release-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Removing server key from known hosts"
  ssh-keygen -f "${HOME}/.ssh/known_hosts" -R "[localhost]:${DOCKER_SSH_PORT}"
}

main "$@"
