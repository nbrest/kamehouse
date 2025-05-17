#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing docker-functions.sh" ; exit 99 ; fi

mainProcess() {
  ssh-keyscan ${DOCKER_HOST_IP} >> ~/.ssh/known_hosts
  setIsLinuxDockerHost
  IS_REMOTE_LINUX_HOST=${IS_LINUX_DOCKER_HOST}
  SSH_USER="${DOCKER_HOST_USERNAME}"
  SSH_SERVER="${DOCKER_HOST_IP}"
  SSH_COMMAND="echo 'ssh from docker container to host successful'"
  executeSshCommand --skip-exit-code-check
}

main "$@"
