parseDockerOs() {
  while getopts ":o:" OPT; do
    case $OPT in
    ("o")
      DOCKER_ENVIRONMENT=$OPTARG
      ;;
    esac
  done
  unset OPTIND
}

parseDockerProfile() {
  while getopts ":p:" OPT; do
    case $OPT in
    ("p")
      DOCKER_PROFILE=$OPTARG
      ;;
    esac
  done
  unset OPTIND
}

setEnvForDockerOs() {
  if [ "${DOCKER_ENVIRONMENT}" != "ubuntu" ] &&
    [ "${DOCKER_ENVIRONMENT}" != "pi" ]; then
    log.error "Option -o [os] has an invalid value of ${DOCKER_ENVIRONMENT}"
    printHelp
    exitProcess 1
  fi

  if [ "${DOCKER_ENVIRONMENT}" == "pi" ]; then
    DOCKER_COMMAND="docker run --privileged --rm"
    DOCKER_IMAGE_BASE="arm32v7/ubuntu:20.04"
    DOCKER_IMAGE_TAG="latest-pi"
  fi  
}

setEnvForDockerProfile() {
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

printDockerOsOption() {
  addHelpOption "-o ${DOCKER_OS_LIST}" "default value is ${DEFAULT_DOCKER_OS}"
}

printDockerProfileOption() {
  addHelpOption "-p ${DOCKER_PROFILES_LIST}" "default profile is ${DEFAULT_DOCKER_PROFILE}"
}