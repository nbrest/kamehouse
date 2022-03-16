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

# Global variables
LOG_PROCESS_TO_FILE=false
DOCKER_PORT_HTTP=7080
SERVICE="kamehouse-docker"
SERVICE_STARTUP="${HOME}/my.scripts/kamehouse/docker/docker-run-java-web-kamehouse.sh"
PROFILE="prod"
DOCKER_ENVIRONMENT="ubuntu"

mainProcess() {
  PID=`sudo netstat -nltp | grep ${DOCKER_PORT_HTTP} | awk '{print $7}' | cut -d '/' -f 1`
  if [ -z ${PID} ]; then
    log.info "${SERVICE} not running. Starting it now"
    ${SERVICE_STARTUP} -p ${PROFILE} -o ${DOCKER_ENVIRONMENT} &
  else
    log.info "${SERVICE} with profile ${PROFILE} is currently running with pid ${COL_PURPLE}${PID}"
  fi
}

parseArguments() {
  while getopts ":ho:p:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
    ("o")
      DOCKER_ENVIRONMENT=$OPTARG
      ;;
    ("p")
      PROFILE=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done

  if [ "${DOCKER_ENVIRONMENT}" != "ubuntu" ] &&
    [ "${DOCKER_ENVIRONMENT}" != "pi" ]; then
    log.error "Option -o [os] has an invalid value of ${DOCKER_ENVIRONMENT}"
    printHelp
    exitProcess 1
  fi

  if [ "${PROFILE}" != "ci" ] &&
    [ "${PROFILE}" != "dev" ] &&
    [ "${PROFILE}" != "demo" ] &&
    [ "${PROFILE}" != "prod" ] &&
    [ "${PROFILE}" != "prod-80-443" ]; then
    log.error "Option -p [profile] has an invalid value of ${DOCKER_BASE_OS}"
    printHelp
    exitProcess 1
  fi
  
  if [ "${PROFILE}" == "ci" ]; then
    DOCKER_PORT_HTTP=15080
  fi

  if [ "${PROFILE}" == "demo" ]; then
    DOCKER_PORT_HTTP=12080
  fi

  if [ "${PROFILE}" == "dev" ]; then
    DOCKER_PORT_HTTP=6080
  fi

  if [ "${PROFILE}" == "prod" ]; then
    DOCKER_PORT_HTTP=7080
  fi

  if [ "${PROFILE}" == "prod-80-443" ]; then
    DOCKER_PORT_HTTP=7080
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-o (ubuntu|pi)${COL_NORMAL} default value is ubuntu"
  echo -e "     ${COL_BLUE}-p (ci|dev|demo|prod|prod-80-443)${COL_NORMAL} default profile is dev"
}

main "$@"
