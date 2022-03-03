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

BUILD_ON_STARTUP=false
BUILD_ON_STARTUP_PARAM=""
DEBUG_MODE=false
DEBUG_MODE_PARAM=""
DOCKER_COMMAND="docker run --rm"
DOCKER_CONTROL_HOST=false
DOCKER_CONTROL_HOST_PARAM=""
DOCKER_BASE_OS="ubuntu"
DOCKER_HOST_IP=""
DOCKER_HOST_HOSTNAME=""
DOCKER_HOST_SUBNET=""
DOCKER_IMAGE_HOSTNAME=""
DOCKER_IMAGE_TAG="latest"
EXPORT_NATIVE_HTTPD=false
PROFILE="dev"
USE_VOLUMES=false
USE_VOLUMES_PARAM=""

mainProcess() {
  setEnvironment
  printEnv
  runDockerImage  
}

setEnvironment() {
  if ${IS_LINUX_HOST}; then
    DOCKER_HOST_OS="linux"
    IS_LINUX_DOCKER_HOST=true
  else
    DOCKER_HOST_OS="windows"
    IS_LINUX_DOCKER_HOST=false
  fi
  DOCKER_HOST_USERNAME=`whoami`
  DOCKER_HOST_IP=`getKameHouseDockerHostIp ${DOCKER_HOST_SUBNET}`
  DOCKER_HOST_HOSTNAME=`hostname`

  if [ -n "${DOCKER_HOST_HOSTNAME}" ]; then
    DOCKER_IMAGE_HOSTNAME=${DOCKER_HOST_HOSTNAME}"-docker"
  fi
}

printEnv() {
  log.info "Environment passed to the container"
  echo ""
  log.info "BUILD_ON_STARTUP=${BUILD_ON_STARTUP}"
  log.info "DEBUG_MODE=${DEBUG_MODE}"
  log.info "DOCKER_BASE_OS=${DOCKER_BASE_OS}"
  log.info "DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST}"
  log.info "DOCKER_HOST_IP=${DOCKER_HOST_IP}"
  log.info "DOCKER_HOST_HOSTNAME=${DOCKER_HOST_HOSTNAME}"
  log.info "DOCKER_HOST_OS=${DOCKER_HOST_OS}"
  log.info "DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME}"
  log.info "DOCKER_PORT_SSH=${DOCKER_PORT_SSH}"
  log.info "DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP}"
  log.info "DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS}"
  log.info "DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG}"
  log.info "DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT}"
  log.info "DOCKER_PORT_MYSQL=${DOCKER_PORT_MYSQL}"
  log.info "EXPORT_NATIVE_HTTPD=${EXPORT_NATIVE_HTTPD}"
  log.info "IS_DOCKER_CONTAINER=${IS_DOCKER_CONTAINER}"
  log.info "IS_LINUX_DOCKER_HOST=${IS_LINUX_DOCKER_HOST}"
  log.info "PROFILE=${PROFILE}"
  log.info "USE_VOLUMES=${USE_VOLUMES}"
  echo ""
}

runDockerImage() {
  log.info "Running image nbrest/java.web.kamehouse:${DOCKER_IMAGE_TAG}"
  log.info "This temporary container will be removed when it exits"

  DOCKER_COMMAND=${DOCKER_COMMAND}"\
      -h ${DOCKER_IMAGE_HOSTNAME} \
      --env BUILD_ON_STARTUP=${BUILD_ON_STARTUP} \
      --env DEBUG_MODE=${DEBUG_MODE} \
      --env DOCKER_BASE_OS=${DOCKER_BASE_OS} \
      --env DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST} \
      --env DOCKER_HOST_IP=${DOCKER_HOST_IP} \
      --env DOCKER_HOST_HOSTNAME=${DOCKER_HOST_HOSTNAME} \
      --env DOCKER_HOST_OS=${DOCKER_HOST_OS} \
      --env DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME} \
      --env DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP} \
      --env DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS} \
      --env DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG} \
      --env DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT} \
      --env DOCKER_PORT_MYSQL=${DOCKER_PORT_MYSQL} \
      --env DOCKER_PORT_SSH=${DOCKER_PORT_SSH} \
      --env IS_DOCKER_CONTAINER=${IS_DOCKER_CONTAINER} \
      --env IS_LINUX_DOCKER_HOST=${IS_LINUX_DOCKER_HOST} \
      --env EXPORT_NATIVE_HTTPD=${EXPORT_NATIVE_HTTPD} \
      --env PROFILE=${PROFILE} \
      --env USE_VOLUMES=${USE_VOLUMES} \
      -p ${DOCKER_PORT_SSH}:22 \
      -p ${DOCKER_PORT_HTTP}:80 \
      -p ${DOCKER_PORT_HTTPS}:443 \
      -p ${DOCKER_PORT_TOMCAT_DEBUG}:${TOMCAT_DEBUG_PORT} \
      -p ${DOCKER_PORT_TOMCAT}:${TOMCAT_PORT} \
      -p ${DOCKER_PORT_MYSQL}:3306 \
      "
  if ${EXPORT_NATIVE_HTTPD}; then
    log.info "Exporting ports 80 and 443 from the container"
    DOCKER_COMMAND=${DOCKER_COMMAND}"\
    -p 80:80 \
    -p 443:443 \
    "
  fi

  if ${USE_VOLUMES}; then
    log.info "Container data will be persisted in volumes (home-kamehouse, home-home-synced, home-ssh)"
    DOCKER_COMMAND=${DOCKER_COMMAND}"\
    -v mysql-data:/var/lib/mysql \
    -v home-kamehouse:/home/nbrest/.kamehouse \
    -v home-home-synced:/home/nbrest/home-synced \
    -v home-ssh:/home/nbrest/.ssh \
    "
  else 
    log.info "Container data will NOT be persisted in volumes"
  fi
  
  DOCKER_COMMAND=${DOCKER_COMMAND}"\
    nbrest/java.web.kamehouse:${DOCKER_IMAGE_TAG}
  "
  
  echo ""
  ${DOCKER_COMMAND}
}

parseArguments() {
  while getopts ":bcdho:p:s:v" OPT; do
    case $OPT in
    ("b")
      BUILD_ON_STARTUP_PARAM=true
      ;;
    ("c")
      DOCKER_CONTROL_HOST_PARAM=true      
      ;;
    ("d")
      DEBUG_MODE_PARAM=true      
      ;;
    ("h")
      parseHelp
      ;;
    ("o")
      DOCKER_BASE_OS=$OPTARG
      ;;
    ("p")
      PROFILE=$OPTARG
      ;;
    ("s")
      DOCKER_HOST_SUBNET=$OPTARG      
      ;;
    ("v")
      USE_VOLUMES_PARAM=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done

  if [ "${DOCKER_BASE_OS}" != "ubuntu" ] &&
    [ "${DOCKER_BASE_OS}" != "pi" ]; then
    log.error "Option -o [os] has an invalid value of ${DOCKER_BASE_OS}"
    printHelp
    exitProcess 1
  fi
  
  if [ "${DOCKER_BASE_OS}" == "pi" ]; then
    DOCKER_COMMAND="docker run --privileged --rm"
    DOCKER_IMAGE_TAG="latest-pi"
  fi

  buildProfile
  overrideDefaultValues
}

buildProfile() {
  if [ "${PROFILE}" != "ci" ] &&
    [ "${PROFILE}" != "dev" ] &&
    [ "${PROFILE}" != "prod" ] &&
    [ "${PROFILE}" != "prod-80-443" ]; then
    log.error "Option -p [profile] has an invalid value of ${DOCKER_BASE_OS}"
    printHelp
    exitProcess 1
  fi

  if [ "${PROFILE}" == "ci" ]; then
    DOCKER_PORT_SSH=15022
    DOCKER_PORT_HTTP=15080
    DOCKER_PORT_HTTPS=15443
    DOCKER_PORT_TOMCAT_DEBUG=15000
    DOCKER_PORT_TOMCAT=15090
    DOCKER_PORT_MYSQL=15306
    BUILD_ON_STARTUP=true
    DEBUG_MODE=false
    DOCKER_CONTROL_HOST=false
    USE_VOLUMES=false
    EXPORT_NATIVE_HTTPD=false
  fi

  if [ "${PROFILE}" == "dev" ]; then
    # Use default ports in the 6000 range
    BUILD_ON_STARTUP=false
    DEBUG_MODE=true
    DOCKER_CONTROL_HOST=false
    USE_VOLUMES=false
    EXPORT_NATIVE_HTTPD=false
  fi

  if [ "${PROFILE}" == "prod" ]; then
    DOCKER_PORT_SSH=7022
    DOCKER_PORT_HTTP=7080
    DOCKER_PORT_HTTPS=7443
    DOCKER_PORT_TOMCAT_DEBUG=7000
    DOCKER_PORT_TOMCAT=7090
    DOCKER_PORT_MYSQL=7306
    BUILD_ON_STARTUP=false
    DEBUG_MODE=false
    DOCKER_CONTROL_HOST=true
    USE_VOLUMES=true
    EXPORT_NATIVE_HTTPD=false
  fi

  if [ "${PROFILE}" == "prod-80-443" ]; then
    DOCKER_PORT_SSH=7022
    DOCKER_PORT_HTTP=7080
    DOCKER_PORT_HTTPS=7443
    DOCKER_PORT_TOMCAT_DEBUG=7000
    DOCKER_PORT_TOMCAT=7090
    DOCKER_PORT_MYSQL=7306
    BUILD_ON_STARTUP=false
    DEBUG_MODE=false
    DOCKER_CONTROL_HOST=true
    USE_VOLUMES=true
    EXPORT_NATIVE_HTTPD=true
  fi
}

overrideDefaultValues() {
  if [ -n "${BUILD_ON_STARTUP_PARAM}" ]; then
    BUILD_ON_STARTUP=${BUILD_ON_STARTUP_PARAM}
  fi

  if [ -n "${DOCKER_CONTROL_HOST_PARAM}" ]; then
    DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST_PARAM}
  fi

  if [ -n "${DEBUG_MODE_PARAM}" ]; then
    DEBUG_MODE=${DEBUG_MODE_PARAM}
  fi

  if [ -n "${USE_VOLUMES_PARAM}" ]; then
    USE_VOLUMES=${USE_VOLUMES_PARAM}
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-b${COL_NORMAL} build and deploy kamehouse on startup"
  echo -e "     ${COL_BLUE}-c${COL_NORMAL} control host through ssh. by default it runs standalone executing all commands within the container"
  echo -e "     ${COL_BLUE}-d${COL_NORMAL} debug. start tomcat in debug mode"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-o (ubuntu|pi)${COL_NORMAL} default base os is ubuntu"
  echo -e "     ${COL_BLUE}-p (ci|dev|prod|prod-80-443)${COL_NORMAL} default profile is dev"
  echo -e "     ${COL_BLUE}-s${COL_NORMAL} docker subnet to determine host ip. Default: ${DOCKER_HOST_DEFAULT_SUBNET}"
  echo -e "     ${COL_BLUE}-v${COL_NORMAL} use volumes to persist data"
}

main "$@"