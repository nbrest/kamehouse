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
  checkIfContainerIsRunning
  exportMysqlDataOnDocker
  copyDataFromContainerToHost
}

checkIfContainerIsRunning() {
  ssh -p ${DOCKER_PORT_SSH} nbrest@localhost -C 'ls' > /dev/null
  if [ "$?" != "0" ]; then
    log.error "Can't connect to container. Exiting process"
    exit 1
  fi
}

exportMysqlDataOnDocker() {
	log.info "Exporting mysql data from mysql server on docker container"
  ssh -p ${DOCKER_PORT_SSH} nbrest@localhost -C '/home/nbrest/my.scripts/kamehouse/mysql-csv-kamehouse.sh'
  ssh -p ${DOCKER_PORT_SSH} nbrest@localhost -C '/home/nbrest/my.scripts/kamehouse/mysql-dump-kamehouse.sh'
}

copyDataFromContainerToHost() {
	log.info "Exporting data from container to host"
  mkdir -p ${HOME}/home-synced/docker/mysql
  rm -rf ${HOME}/home-synced/docker/mysql
  scp -C -r -P ${DOCKER_PORT_SSH} localhost:/home/nbrest/home-synced/mysql ${HOME}/home-synced/docker/mysql
}

main "$@"