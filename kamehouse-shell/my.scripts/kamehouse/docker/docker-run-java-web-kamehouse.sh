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

DEBUG_MODE=false
FAST_DOCKER_INIT=false
PERSISTENT_DATA=false
DOCKER_CONTROL_HOST=false
DOCKER_HOST_IP=""
DOCKER_HOST_SUBNET=""

mainProcess() {
  setEnvironment
  runDockerImage  
}

setEnvironment() {
  if ${IS_LINUX_HOST}; then
    DOCKER_HOST_OS="linux"
  else
    DOCKER_HOST_OS="windows"
  fi
  DOCKER_HOST_USERNAME=`whoami`
  DOCKER_HOST_IP=`getKameHouseDockerHostIp ${DOCKER_HOST_SUBNET}`
  
  log.info "Environment passed to the container"
  echo ""
  log.info "FAST_DOCKER_INIT=${FAST_DOCKER_INIT}"
  log.info "PERSISTENT_DATA=${PERSISTENT_DATA}"
  log.info "DEBUG_MODE=${DEBUG_MODE}"
  log.info "DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST}"
  log.info "DOCKER_HOST_IP=${DOCKER_HOST_IP}"
  log.info "DOCKER_HOST_OS=${DOCKER_HOST_OS}"
  log.info "DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME}"
  echo ""
}

runDockerImage() {
  log.info "Running image nbrest/java.web.kamehouse:latest"
  log.info "This temporary container will be removed when it exits"

  if ${PERSISTENT_DATA}; then
    log.info "Container data will be persisted in volumes"
    echo ""
    docker run --rm \
      --env FAST_DOCKER_INIT=${FAST_DOCKER_INIT} \
      --env PERSISTENT_DATA=${PERSISTENT_DATA} \
      --env DEBUG_MODE=${DEBUG_MODE} \
      --env DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST} \
      --env DOCKER_HOST_IP=${DOCKER_HOST_IP} \
      --env DOCKER_HOST_OS=${DOCKER_HOST_OS} \
      --env DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME} \
      -p ${DOCKER_PORT_SSH}:22 \
      -p ${DOCKER_PORT_HTTP}:80 \
      -p ${DOCKER_PORT_HTTPS}:443 \
      -p ${DOCKER_PORT_TOMCAT_DEBUG}:${TOMCAT_DEBUG_PORT} \
      -p ${DOCKER_PORT_TOMCAT}:${TOMCAT_PORT} \
      -p ${DOCKER_PORT_MYSQL}:3306 \
      -v mysql-data:/var/lib/mysql \
      -v home-kamehouse:/home/nbrest/.kamehouse \
      -v home-home-synced:/home/nbrest/home-synced \
      -v home-ssh:/home/nbrest/.ssh \
      nbrest/java.web.kamehouse:latest
  else
    echo ""
    docker run --rm \
      --env FAST_DOCKER_INIT=${FAST_DOCKER_INIT} \
      --env PERSISTENT_DATA=${PERSISTENT_DATA} \
      --env DEBUG_MODE=${DEBUG_MODE} \
      --env DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST} \
      --env DOCKER_HOST_IP=${DOCKER_HOST_IP} \
      --env DOCKER_HOST_OS=${DOCKER_HOST_OS} \
      --env DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME} \
      -p ${DOCKER_PORT_SSH}:22 \
      -p ${DOCKER_PORT_HTTP}:80 \
      -p ${DOCKER_PORT_HTTPS}:443 \
      -p ${DOCKER_PORT_TOMCAT_DEBUG}:${TOMCAT_DEBUG_PORT} \
      -p ${DOCKER_PORT_TOMCAT}:${TOMCAT_PORT} \
      -p ${DOCKER_PORT_MYSQL}:3306 \
      nbrest/java.web.kamehouse:latest
  fi
}

parseArguments() {
  while getopts ":cdfhps:" OPT; do
    case $OPT in
    ("c")
      DOCKER_CONTROL_HOST=true      
      ;;
    ("d")
      DEBUG_MODE=true      
      ;;
    ("f")
      FAST_DOCKER_INIT=true      
      ;;
    ("h")
      parseHelp
      ;;
    ("p")
      PERSISTENT_DATA=true
      ;;
    ("s")
      DOCKER_HOST_SUBNET=$OPTARG      
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
  echo -e "     ${COL_BLUE}-c${COL_NORMAL} control host through ssh. by default it runs standalone executing all commands within the container"
  echo -e "     ${COL_BLUE}-d${COL_NORMAL} debug. start tomcat in debug mode"
  echo -e "     ${COL_BLUE}-f${COL_NORMAL} fast startup. skip pull and rebuild kamehouse on startup"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-p${COL_NORMAL} persistent container. uses volumes to persist data"
  echo -e "     ${COL_BLUE}-s${COL_NORMAL} docker subnet to determine host ip. Default: ${DOCKER_HOST_DEFAULT_SUBNET}"
}

main "$@"