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
  ssh-keyscan ${DOCKER_HOST_IP} >> ~/.ssh/known_hosts
  setIsLinuxDockerHost
  IS_REMOTE_LINUX_HOST=${IS_LINUX_DOCKER_HOST}
  SSH_USER="${DOCKER_HOST_USERNAME}"
  SSH_SERVER="${DOCKER_HOST_IP}"
  SSH_COMMAND="echo 'ssh from docker container to host successful'"
  executeSshCommand --skip-exit-code-check
}

main "$@"
