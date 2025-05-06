#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 99
fi

LOAD_KAMEHOUSE_SECRETS=true
BUILD_ON_STARTUP=false
BUILD_ON_STARTUP_PARAM=""
DEBUG_MODE=false
DEBUG_MODE_PARAM=""
DOCKER_COMMAND="docker run --rm"
DOCKER_CONTROL_HOST=false
DOCKER_CONTROL_HOST_PARAM=""
DOCKER_IMAGE_HOSTNAME=""
USE_VOLUMES=false
USE_VOLUMES_PARAM=""

mainProcess() {
  setEnvironment
  printEnv
  runDockerImage  
}

setEnvironment() {
  if [ -z "${DOCKER_HOST_AUTH}" ]; then
    log.info "DOCKER_HOST_AUTH is NOT set in ${HOME}/.kamehouse/config/kamehouse.cfg. Using GROOT_API_BASIC_AUTH value set in .kamehouse-secrets.cfg"
    DOCKER_HOST_AUTH=${GROOT_API_BASIC_AUTH}
  fi 

  if [ -z "${DOCKER_HOST_IP}" ]; then
    log.error "DOCKER_HOST_IP needs to be set in ${HOME}/.kamehouse/config/kamehouse.cfg"
    exitProcess ${EXIT_INVALID_CONFIG}
  fi 
  
  if [ -z "${DOCKER_HOST_HOSTNAME}" ]; then
    log.error "DOCKER_HOST_HOSTNAME needs to be set in ${HOME}/.kamehouse/config/kamehouse.cfg"
    exitProcess ${EXIT_INVALID_CONFIG}
  fi 

  if [ -z "${DOCKER_HOST_USERNAME}" ]; then
    log.error "DOCKER_HOST_USERNAME needs to be set in ${HOME}/.kamehouse/config/kamehouse.cfg"
    exitProcess ${EXIT_INVALID_CONFIG}
  fi 

  if [ -z "${DOCKER_HOST_OS}" ]; then
    log.error "DOCKER_HOST_OS needs to be set in ${HOME}/.kamehouse/config/kamehouse.cfg"
    exitProcess ${EXIT_INVALID_CONFIG}
  fi 

  if [ -z "${DOCKER_HOST_PLAYLISTS_PATH}" ]; then
    log.info "DOCKER_HOST_PLAYLISTS_PATH is NOT set in ${HOME}/.kamehouse/config/kamehouse.cfg. Using PLAYLISTS_PATH value"
    DOCKER_HOST_PLAYLISTS_PATH=${PLAYLISTS_PATH}
  fi 

  if [ -z "${DOCKER_HOST_PORT}" ]; then
    log.error "DOCKER_HOST_PORT needs to be set in ${HOME}/.kamehouse/config/kamehouse.cfg"
    exitProcess ${EXIT_INVALID_CONFIG}
  fi 

  setIsLinuxDockerHost

  if [ -n "${DOCKER_HOST_HOSTNAME}" ]; then
    DOCKER_IMAGE_HOSTNAME=${DOCKER_HOST_HOSTNAME}"-docker"
    if [ -n "${DOCKER_PROFILE}" ]; then
      DOCKER_IMAGE_HOSTNAME=${DOCKER_IMAGE_HOSTNAME}"-"${DOCKER_PROFILE}
    fi
  fi
}

printEnv() {
  log.info "Environment passed to the container"
  echo ""
  log.info "BUILD_ON_STARTUP=${BUILD_ON_STARTUP}"
  log.info "DEBUG_MODE=${DEBUG_MODE}"
  log.info "DOCKER_BASE_OS=${DOCKER_ENVIRONMENT}"
  log.info "DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST}"
  log.info "DOCKER_HOST_AUTH=****"
  log.info "DOCKER_HOST_IP=${DOCKER_HOST_IP}"
  log.info "DOCKER_HOST_HOSTNAME=${DOCKER_HOST_HOSTNAME}"
  log.info "DOCKER_HOST_OS=${DOCKER_HOST_OS}"
  log.info "DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME}"
  log.info "DOCKER_HOST_PLAYLISTS_PATH=${DOCKER_HOST_PLAYLISTS_PATH}"
  log.info "DOCKER_HOST_PORT=${DOCKER_HOST_PORT}"
  log.info "DOCKER_PORT_SSH=${DOCKER_PORT_SSH}"
  log.info "DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP}"
  log.info "DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS}"
  log.info "DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG}"
  log.info "DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT}"
  log.info "DOCKER_PORT_CMD_LINE_DEBUG=${DOCKER_PORT_CMD_LINE_DEBUG}"
  log.info "DOCKER_PORT_MARIADB=${DOCKER_PORT_MARIADB}"
  log.info "IS_DOCKER_CONTAINER=${IS_DOCKER_CONTAINER}"
  log.info "IS_LINUX_DOCKER_HOST=${IS_LINUX_DOCKER_HOST}"
  log.info "DOCKER_PROFILE=${DOCKER_PROFILE}"
  log.info "USE_VOLUMES=${USE_VOLUMES}"
  echo ""
}

runDockerImage() {
  log.info "Running image nbrest/kamehouse:${DOCKER_IMAGE_TAG}"
  log.info "This temporary container will be removed when it exits"

  DOCKER_COMMAND=${DOCKER_COMMAND}"\
      --name ${DOCKER_IMAGE_HOSTNAME}-kamehouse \
      -h ${DOCKER_IMAGE_HOSTNAME} \
      --env BUILD_ON_STARTUP=${BUILD_ON_STARTUP} \
      --env DEBUG_MODE=${DEBUG_MODE} \
      --env DOCKER_BASE_OS=${DOCKER_ENVIRONMENT} \
      --env DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST} \
      --env DOCKER_HOST_AUTH=${DOCKER_HOST_AUTH} \
      --env DOCKER_HOST_IP=${DOCKER_HOST_IP} \
      --env DOCKER_HOST_HOSTNAME=${DOCKER_HOST_HOSTNAME} \
      --env DOCKER_HOST_OS=${DOCKER_HOST_OS} \
      --env DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME} \
      --env DOCKER_HOST_PLAYLISTS_PATH=${DOCKER_HOST_PLAYLISTS_PATH} \
      --env DOCKER_HOST_PORT=${DOCKER_HOST_PORT} \
      --env DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP} \
      --env DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS} \
      --env DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG} \
      --env DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT} \
      --env DOCKER_PORT_CMD_LINE_DEBUG=${DOCKER_PORT_CMD_LINE_DEBUG}
      --env DOCKER_PORT_MARIADB=${DOCKER_PORT_MARIADB} \
      --env DOCKER_PORT_SSH=${DOCKER_PORT_SSH} \
      --env IS_DOCKER_CONTAINER=${IS_DOCKER_CONTAINER} \
      --env IS_LINUX_DOCKER_HOST=${IS_LINUX_DOCKER_HOST} \
      --env DOCKER_PROFILE=${DOCKER_PROFILE} \
      --env USE_VOLUMES=${USE_VOLUMES} \
      -p ${DOCKER_PORT_SSH}:22 \
      -p ${DOCKER_PORT_HTTP}:80 \
      -p ${DOCKER_PORT_HTTPS}:443 \
      -p ${DOCKER_PORT_TOMCAT_DEBUG}:${TOMCAT_DEBUG_PORT} \
      -p ${DOCKER_PORT_TOMCAT}:${TOMCAT_PORT} \
      -p ${DOCKER_PORT_CMD_LINE_DEBUG}:${CMD_LINE_DEBUG_PORT} \
      -p ${DOCKER_PORT_MARIADB}:3306 \
      "

  if ${USE_VOLUMES}; then
    log.info "Container data will be persisted in volumes: mariadb-data-${DOCKER_PROFILE}, home-kamehouse-${DOCKER_PROFILE}, home-ssh-${DOCKER_PROFILE}"
    DOCKER_COMMAND=${DOCKER_COMMAND}"\
    -v mariadb-data-${DOCKER_PROFILE}:/var/lib/mysql \
    -v home-kamehouse-${DOCKER_PROFILE}:/home/${DOCKER_USERNAME}/.kamehouse \
    -v home-ssh-${DOCKER_PROFILE}:/home/${DOCKER_USERNAME}/.ssh \
    "
  else 
    log.info "Container data will NOT be persisted in volumes"
  fi

  if [ "${DOCKER_PROFILE}" == "dev" ]; then
    local HOST_USERHOME=`getHostUserHomeGitBash`
    log.info "Mounting ${HOST_USERHOME}/workspace/kamehouse to /home/${DOCKER_USERNAME}/git/kamehouse"
    DOCKER_COMMAND=${DOCKER_COMMAND}"\
    -v ${HOST_USERHOME}/workspace/kamehouse:/home/${DOCKER_USERNAME}/git/kamehouse \
    "
  fi
  
  DOCKER_COMMAND=${DOCKER_COMMAND}"\
    nbrest/kamehouse:${DOCKER_IMAGE_TAG}
  "
  
  echo ""
  log.debug "${DOCKER_COMMAND}"
  ${DOCKER_COMMAND}
}

configureDockerProfile() {
  if [ "${DOCKER_PROFILE}" == "ci" ]; then
    BUILD_ON_STARTUP=true
    DEBUG_MODE=false
    DOCKER_CONTROL_HOST=false
    USE_VOLUMES=false
  fi

  if [ "${DOCKER_PROFILE}" == "demo" ]; then
    BUILD_ON_STARTUP=true
    DEBUG_MODE=false
    DOCKER_CONTROL_HOST=false
    USE_VOLUMES=false
  fi

  if [ "${DOCKER_PROFILE}" == "dev" ]; then
    BUILD_ON_STARTUP=false
    DEBUG_MODE=true
    DOCKER_CONTROL_HOST=false
    USE_VOLUMES=false
  fi

  if [ "${DOCKER_PROFILE}" == "prod" ]; then
    BUILD_ON_STARTUP=true
    DEBUG_MODE=false
    DOCKER_CONTROL_HOST=true
    USE_VOLUMES=true
  fi

  if [ "${DOCKER_PROFILE}" == "tag" ]; then
    BUILD_ON_STARTUP=false
    DEBUG_MODE=false
    DOCKER_CONTROL_HOST=false
    USE_VOLUMES=false
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

parseArguments() {
  parseDockerProfile "$@"
  parseDockerTag "$@"

  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -p|-t)
        # parsed in a previous parse options function 
        ;;
      -b|--build)
        BUILD_ON_STARTUP_PARAM=true
        ;;
      -c|--control-host)
        DOCKER_CONTROL_HOST_PARAM=true
        ;;
      -d|--debug)
        DEBUG_MODE_PARAM=true
        ;;
      -f|--fast-start)
        BUILD_ON_STARTUP_PARAM=false
        ;;
      -v|--use-volumes)
        USE_VOLUMES_PARAM=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  setEnvForDockerTag
  setEnvForDockerProfile
  configureDockerProfile
  overrideDefaultValues

  if [ "${DOCKER_PROFILE}" == "tag" ]; then
    if [ "${DOCKER_IMAGE_TAG}" == "latest" ]; then
      log.error "Set a valid -t [tag] when selecting docker profile -p tag"
      printHelp
      exitProcess ${EXIT_INVALID_ARG}
    fi
  fi
}

printHelpOptions() {
  addHelpOption "-b --build" "build and deploy kamehouse on startup"
  addHelpOption "-c --control-host" "control docker container host. by default it runs standalone executing all commands within the container"
  addHelpOption "-d --debug" "debug. start tomcat in debug mode"
  addHelpOption "-f --fast-startup" "fast startup. don't build and deploy"
  printDockerProfileOption
  printDockerTagOption
  addHelpOption "-v --use-volumes" "use volumes to persist data"
}

main "$@"
