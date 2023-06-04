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

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=true
BUILD_DATE_KAMEHOUSE="0000-00-00"

mainProcess() {
  log.info "Building docker image nbrest/kamehouse:${DOCKER_IMAGE_TAG} and push it to docker hub"
  mkdir -p ${HOME}/.docker-cache
  log.debug "docker buildx create --platform linux/amd64,linux/arm/v7 --name kamehouse-builder --bootstrap --use"
  docker buildx create --platform linux/amd64,linux/arm/v7 --name kamehouse-builder --bootstrap --use
  log.debug "docker buildx build --cache-from=type=local,src=${HOME}/.docker-cache --cache-to=type=local,dest=${HOME}/.docker-cache --platform=linux/amd64,linux/arm/v7 --build-arg DOCKER_IMAGE_BASE=${DOCKER_IMAGE_BASE} --build-arg BUILD_DATE_KAMEHOUSE=${BUILD_DATE_KAMEHOUSE} -t nbrest/kamehouse:${DOCKER_IMAGE_TAG} --push ."
  docker buildx build \
    --cache-from=type=local,src=${HOME}/.docker-cache \
    --cache-to=type=local,dest=${HOME}/.docker-cache \
    --platform=linux/amd64,linux/arm/v7 \
    --build-arg BUILD_DATE_KAMEHOUSE="${BUILD_DATE_KAMEHOUSE}" \
    --build-arg DOCKER_IMAGE_BASE=${DOCKER_IMAGE_BASE} \
    --push \
    -t nbrest/kamehouse:${DOCKER_IMAGE_TAG} .
}

parseArguments() {
   while getopts ":b" OPT; do
    case $OPT in
    ("b")
      BUILD_DATE_KAMEHOUSE=$(date)
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

printHelpOptions() {
  addHelpOption "-b" "force build of kamehouse. Skip docker cache from build step"
}

main "$@"
