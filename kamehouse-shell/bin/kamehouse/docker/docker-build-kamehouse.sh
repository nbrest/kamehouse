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

# When I update the base image here also update docker-setup.md
DOCKER_IMAGE_BASE="ubuntu:20.04"
DOCKER_IMAGE_TAG="latest"
DOCKER_ENVIRONMENT="ubuntu"

mainProcess() {
  log.info "Building docker image nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  log.debug "docker build --build-arg DOCKER_IMAGE_BASE=${DOCKER_IMAGE_BASE} -t nbrest/kamehouse:${DOCKER_IMAGE_TAG} ."
  docker build --build-arg DOCKER_IMAGE_BASE=${DOCKER_IMAGE_BASE} -t nbrest/kamehouse:${DOCKER_IMAGE_TAG} .
}

parseArguments() {
  while getopts ":ho:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
    ("o")
      DOCKER_ENVIRONMENT=$OPTARG
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

  if [ "${DOCKER_ENVIRONMENT}" == "pi" ]; then
    DOCKER_IMAGE_BASE="arm32v7/ubuntu:20.04"
    DOCKER_IMAGE_TAG="latest-pi"
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-o (ubuntu|pi)${COL_NORMAL} default value is ubuntu"
}

main "$@"
