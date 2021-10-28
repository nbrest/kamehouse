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

PULL_KAMEHOUSE=true
PERSISTENT_CONTAINER=false
KAMEHOUSE_HOST_IP=""
KAMEHOUSE_SUBNET=""

mainProcess() {
  log.info "Running image nbrest/java.web.kamehouse:latest"
  log.warn "This temporary container will be removed when it exits"
  log.info "Environment"
  log.info "PULL_KAMEHOUSE=${PULL_KAMEHOUSE}"
  log.info "PERSISTENT_CONTAINER=${PERSISTENT_CONTAINER}"
  KAMEHOUSE_HOST_IP=`getKameHouseDockerHostIp ${KAMEHOUSE_SUBNET}`
  log.info "KAMEHOUSE_HOST_IP=${KAMEHOUSE_HOST_IP}"

  if ${PERSISTENT_CONTAINER}; then
    docker run --rm \
      --env PULL_KAMEHOUSE=${PULL_KAMEHOUSE} \
      --env PERSISTENT_CONTAINER=${PERSISTENT_CONTAINER} \
      --env KAMEHOUSE_HOST_IP=${KAMEHOUSE_HOST_IP} \
      -p ${DOCKER_PORT_SSH}:22 \
      -p ${DOCKER_PORT_HTTP}:80 \
      -p ${DOCKER_PORT_HTTPS}:443 \
      -p ${DOCKER_PORT_TOMCAT}:${TOMCAT_PORT} \
      -v mysql-data:/var/lib/mysql \
      -v home-kamehouse:/home/nbrest/.kamehouse \
      -v home-home-synced:/home/nbrest/home-synced \
      -v home-ssh:/home/nbrest/.ssh \
      nbrest/java.web.kamehouse:latest
  else
    docker run --rm \
      --env PULL_KAMEHOUSE=${PULL_KAMEHOUSE} \
      --env PERSISTENT_CONTAINER=${PERSISTENT_CONTAINER} \
      --env KAMEHOUSE_HOST_IP=${KAMEHOUSE_HOST_IP} \
      -p ${DOCKER_PORT_SSH}:22 \
      -p ${DOCKER_PORT_HTTP}:80 \
      -p ${DOCKER_PORT_HTTPS}:443 \
      -p ${DOCKER_PORT_TOMCAT}:${TOMCAT_PORT} \
      nbrest/java.web.kamehouse:latest
  fi
}

parseArguments() {
  while getopts ":fhps:" OPT; do
    case $OPT in
    ("f")
      PULL_KAMEHOUSE=false      
      ;;
    ("h")
      parseHelp
      ;;
    ("p")
      PERSISTENT_CONTAINER=true
      ;;
    ("s")
      KAMEHOUSE_SUBNET=$OPTARG      
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
  
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-f${COL_NORMAL} fast startup. skip pull and rebuild kamehouse on startup"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-p${COL_NORMAL} persistent container. uses volumes to persist data"
  echo -e "     ${COL_BLUE}-s${COL_NORMAL} subnet to determine host ip. Default: ${KAMEHOUSE_DEFAULT_SUBNET}"
}

main "$@"