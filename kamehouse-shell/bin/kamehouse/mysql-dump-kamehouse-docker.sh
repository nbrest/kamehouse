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

DOCKER_PROFILE="dev"

mainProcess() {
  checkIfContainerIsRunning
  exportMysqlDataOnDocker
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

exportMysqlDataOnDocker() {
	log.info "Exporting mysql data from mysql server on docker container"
  log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"/home/${DOCKER_USERNAME}/programs/kamehouse-shell/binkamehouse/mysql-csv-kamehouse.sh\""
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "/home/${DOCKER_USERNAME}/programs/kamehouse-shell/binkamehouse/mysql-csv-kamehouse.sh"
  
  log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mysql-dump-kamehouse.sh\""
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mysql-dump-kamehouse.sh"
}

copyDataFromContainerToHost() {
	log.info "Exporting data from container to host"
  mkdir -p ${HOME}/home-synced/docker/mysql
  rm -rf ${HOME}/home-synced/docker/mysql
  log.debug "scp -C -r -P ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/mysql ${HOME}/home-synced/docker/mysql"
  scp -C -r -P ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/mysql ${HOME}/home-synced/docker/mysql
}

parseArguments() {
  while getopts ":hp:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
    ("p")
      DOCKER_PROFILE=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done

  if [ "${DOCKER_PROFILE}" != "ci" ] &&
    [ "${DOCKER_PROFILE}" != "dev" ] &&
    [ "${DOCKER_PROFILE}" != "demo" ] &&
    [ "${DOCKER_PROFILE}" != "prod" ] &&
    [ "${DOCKER_PROFILE}" != "prod-ext" ]; then
    log.error "Option -p [profile] has an invalid value of ${DOCKER_PROFILE}"
    printHelp
    exitProcess 1
  fi
  
  if [ "${DOCKER_PROFILE}" == "ci" ]; then
    DOCKER_PORT_SSH=15022
  fi

  if [ "${DOCKER_PROFILE}" == "demo" ]; then
    DOCKER_PORT_SSH=12022
  fi

  if [ "${DOCKER_PROFILE}" == "prod" ]; then
    DOCKER_PORT_SSH=7022
  fi

  if [ "${DOCKER_PROFILE}" == "prod-ext" ]; then
    DOCKER_PORT_SSH=7022
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-p (ci|dev|demo|prod|prod-ext)${COL_NORMAL} default profile is dev"
}

main "$@"
