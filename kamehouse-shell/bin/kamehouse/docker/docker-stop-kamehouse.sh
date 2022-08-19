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

DOCKER_PROFILE="${DEFAULT_DOCKER_PROFILE}"
DOCKER_PORT_SSH=6022
CONTAINER=""

mainProcess() {

  if [ -z "${CONTAINER}" ]; then 
    log.info "Container not passed as argument, attempting to find a running kamehouse container of profile ${COL_PURPLE}${DOCKER_PROFILE}"
    CONTAINER=`docker container list | grep -e "kamehouse\|/bin/sh -c" | grep "${DOCKER_PORT_SSH}" |  cut -d ' ' -f1`
  fi

  if [ -n "${CONTAINER}" ]; then 
    log.info "Stopping container ${COL_PURPLE}${CONTAINER}"
    log.debug "docker stop ${CONTAINER}"
    docker stop ${CONTAINER}
  else
    log.warn "No kamehouse container running detected for profile ${COL_PURPLE}${DOCKER_PROFILE}"
  fi

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-status-kamehouse.sh
}

parseArguments() {
  while getopts ":c:p:" OPT; do
    case $OPT in
    ("c")
      CONTAINER=$OPTARG
      ;;
    ("p")
      DOCKER_PROFILE=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
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

printHelpOptions() {
  addHelpOption "-c (container id)" "id of the container to stop"
  addHelpOption "-p ${DOCKER_PROFILES_LIST}" "default profile is ${DEFAULT_DOCKER_PROFILE}"
}

main "$@"
