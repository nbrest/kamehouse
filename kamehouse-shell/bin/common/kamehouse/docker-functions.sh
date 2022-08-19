parseDockerOs() {
  while getopts ":o:" OPT; do
    case $OPT in
    ("o")
      DOCKER_ENVIRONMENT=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvForDockerOs() {
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

printDockerOsOption() {
  addHelpOption "-o ${DOCKER_OS_LIST}" "default value is ${DEFAULT_DOCKER_OS}"
}