#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/my.scripts/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  log.info "Setting up persistent data in the volumes"

  log.info "Copy home .ssh folder"
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.ssh/* localhost:/home/nbrest/.ssh

  log.info "Copy home .kamehouse folder"
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/.unlock.screen.pwd.enc localhost:/home/nbrest/.kamehouse
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/.vnc.server.pwd.enc localhost:/home/nbrest/.kamehouse

  log.info "Copy home home-synced folder"
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/integration-test-cred.enc localhost:/home/nbrest/home-synced/.kamehouse
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/keys/* localhost:/home/nbrest/home-synced/.kamehouse/keys
}

main "$@"
