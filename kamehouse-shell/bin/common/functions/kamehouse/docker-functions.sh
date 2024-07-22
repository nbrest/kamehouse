DOCKER_USERNAME=${DEFAULT_KAMEHOUSE_USERNAME}
IS_LINUX_DOCKER_HOST=""

DOCKER_SERVER="niko-server"

DOCKER_PORT_SSH_CI=15022
DOCKER_PORT_HTTP_CI=15080
DOCKER_PORT_HTTPS_CI=15443
DOCKER_PORT_TOMCAT_DEBUG_CI=15000
DOCKER_PORT_TOMCAT_CI=15090
DOCKER_PORT_MARIADB_CI=15306

DOCKER_PORT_SSH_DEMO=12022
DOCKER_PORT_HTTP_DEMO=12080
DOCKER_PORT_HTTPS_DEMO=12443
DOCKER_PORT_TOMCAT_DEBUG_DEMO=12000
DOCKER_PORT_TOMCAT_DEMO=12090
DOCKER_PORT_MARIADB_DEMO=12306

DOCKER_PORT_SSH_DEV=6022
DOCKER_PORT_HTTP_DEV=6080
DOCKER_PORT_HTTPS_DEV=6443
DOCKER_PORT_TOMCAT_DEBUG_DEV=6000
DOCKER_PORT_TOMCAT_DEV=6090
DOCKER_PORT_MARIADB_DEV=6306

DOCKER_PORT_SSH_PROD=7022
DOCKER_PORT_HTTP_PROD=7080
DOCKER_PORT_HTTPS_PROD=7443
DOCKER_PORT_TOMCAT_DEBUG_PROD=7000
DOCKER_PORT_TOMCAT_PROD=7090
DOCKER_PORT_MARIADB_PROD=7306

DOCKER_PORT_SSH_TAG=13022
DOCKER_PORT_HTTP_TAG=13080
DOCKER_PORT_HTTPS_TAG=13443
DOCKER_PORT_TOMCAT_DEBUG_TAG=13000
DOCKER_PORT_TOMCAT_TAG=13090
DOCKER_PORT_MARIADB_TAG=13306

DOCKER_PORT_SSH=${DOCKER_PORT_SSH_DEV}
DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP_DEV}
DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS_DEV}
DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG_DEV}
DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT_DEV}
DOCKER_PORT_MARIADB=${DOCKER_PORT_MARIADB_DEV}

DOCKER_PROFILES_LIST="(ci|dev|demo|prod|tag)"
DEFAULT_DOCKER_PROFILE="dev"
DOCKER_PROFILE="${DEFAULT_DOCKER_PROFILE}"

DEFAULT_DOCKER_OS="ubuntu"
DOCKER_ENVIRONMENT="${DEFAULT_DOCKER_OS}"

DOCKER_COMMAND=""

DOCKER_BUILD_RELEASE_TAG=false
DOCKER_TAG_MINIMUM_VERSION="v8.15"
let DOCKER_TAG_MINIMUM_VER_NUMBER=815

# When I update the base image here also update docker-setup.md
DOCKER_IMAGE_BASE="ubuntu:22.04"
DOCKER_IMAGE_TAG="latest"

# This may not give me the correct host ip address if there's another adapter within the same subnet
DOCKER_HOST_DEFAULT_SUBNET="192\.168\.0\.[0-9]\+"
#DOCKER_HOST_DEFAULT_SUBNET="192\.168\.56\.[0-9]\+"

# Get the ip address of the host running kamehouse in a docker container
getKameHouseDockerHostIp() {
  local DOCKER_HOST_SUBNET=$1
  if [ -z "${DOCKER_HOST_SUBNET}" ]; then
    DOCKER_HOST_SUBNET=${DOCKER_HOST_DEFAULT_SUBNET}
  fi

  if ${IS_LINUX_HOST}; then
    echo `ifconfig | grep -e "${DOCKER_HOST_SUBNET}" | grep "inet" | head -n 1 | awk '{print $2}'`
  else
    echo `ipconfig | grep -e "${DOCKER_HOST_SUBNET}" | grep "IPv4" | head -n 1 | awk '{print $14}'`
  fi
}

parseDockerProfile() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -p)
        DOCKER_PROFILE="${ARGS[i+1]}"
        ;;
    esac
  done
}

parseDockerTag() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -t)
        DOCKER_IMAGE_TAG="${ARGS[i+1]}"
        DOCKER_BUILD_RELEASE_TAG=true
        ;;
    esac
  done
}

setEnvForDockerProfile() {
  if [ "${DOCKER_PROFILE}" != "ci" ] &&
      [ "${DOCKER_PROFILE}" != "demo" ] &&
      [ "${DOCKER_PROFILE}" != "dev" ] &&
      [ "${DOCKER_PROFILE}" != "prod" ] &&
      [ "${DOCKER_PROFILE}" != "tag" ]; then
    log.error "Option -p [profile] has an invalid value of ${DOCKER_PROFILE}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi

  case ${DOCKER_PROFILE} in
  "ci")
    DOCKER_PORT_SSH=${DOCKER_PORT_SSH_CI}
    DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP_CI}
    DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS_CI}
    DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG_CI}
    DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT_CI}
    DOCKER_PORT_MARIADB=${DOCKER_PORT_MARIADB_CI}
    ;;
  "demo")
    DOCKER_PORT_SSH=${DOCKER_PORT_SSH_DEMO}
    DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP_DEMO}
    DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS_DEMO}
    DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG_DEMO}
    DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT_DEMO}
    DOCKER_PORT_MARIADB=${DOCKER_PORT_MARIADB_DEMO}
    ;;
  "dev")
    DOCKER_PORT_SSH=${DOCKER_PORT_SSH_DEV}
    DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP_DEV}
    DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS_DEV}
    DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG_DEV}
    DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT_DEV}
    DOCKER_PORT_MARIADB=${DOCKER_PORT_MARIADB_DEV}
    ;;  
  "prod")
    DOCKER_PORT_SSH=${DOCKER_PORT_SSH_PROD}
    DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP_PROD}
    DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS_PROD}
    DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG_PROD}
    DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT_PROD}
    DOCKER_PORT_MARIADB=${DOCKER_PORT_MARIADB_PROD}
    ;;
  "tag")
    DOCKER_PORT_SSH=${DOCKER_PORT_SSH_TAG}
    DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP_TAG}
    DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS_TAG}
    DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG_TAG}
    DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT_TAG}
    DOCKER_PORT_MARIADB=${DOCKER_PORT_MARIADB_TAG}
    ;;
  esac
}

setEnvForDockerTag() {
  if [ "${DOCKER_IMAGE_TAG}" == "latest" ]; then
    return
  fi
  
  local DOCKER_IMAGE_TAG_RX=^v[0-9]+\.[0-9]{2}$
  if [[ "${DOCKER_IMAGE_TAG}" =~ ${DOCKER_IMAGE_TAG_RX} ]]; then
    local DOUBLE_DIGITS_MAIN_RX=^v[0-9]{2}\.[0-9]{2}$
    local TAG_NUMBER_STR="0"
    if [[ "${DOCKER_IMAGE_TAG}" =~ ${DOUBLE_DIGITS_MAIN_RX} ]]; then
      # vXX.XX
      TAG_NUMBER_STR=${DOCKER_IMAGE_TAG:1:2}${DOCKER_IMAGE_TAG:4:2}
    else
      # vX.XX
      TAG_NUMBER_STR=${DOCKER_IMAGE_TAG:1:1}${DOCKER_IMAGE_TAG:3:2}
    fi
    log.debug "TAG_NUMBER_STR=${TAG_NUMBER_STR}"
    local let TAG_NUMBER=$(($TAG_NUMBER_STR))
    log.debug "TAG_NUMBER=${TAG_NUMBER}"
    if [ ${TAG_NUMBER} -ge ${DOCKER_TAG_MINIMUM_VER_NUMBER} ]; then
      log.info "tag ${DOCKER_IMAGE_TAG} is valid"
    else
      log.error "Option -t [tag] has an invalid value of ${DOCKER_IMAGE_TAG}"
      printHelp
      exitProcess ${EXIT_INVALID_ARG}
    fi
  else
    log.error "Option -t [tag] has an invalid value of ${DOCKER_IMAGE_TAG}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi 

  if ${DOCKER_BUILD_RELEASE_TAG}; then
    # For release tags, build the image locally, don't push to docker hub
    PLATFORM="linux/amd64"
    ACTION="--load"
    # Set the tag profile when running an image
    log.info "Overriding docker profile to tag"
    DOCKER_PROFILE="tag"
  fi
}

printDockerProfileOption() {
  addHelpOption "-p ${DOCKER_PROFILES_LIST}" "default profile is ${DEFAULT_DOCKER_PROFILE}"
}

printDockerTagOption() {
  addHelpOption "-t vX.XX" "run this script for a specific KameHouse tag version. Minimum supported tag is ${DOCKER_TAG_MINIMUM_VERSION}"
}

# Loads the environment variables set when running in a docker container
# Look at the docker-init script to see what variables are set in the container env
loadDockerContainerEnv() {
  if [ -f "${CONTAINER_ENV_FILE}" ]; then
    log.debug "Running inside a docker container"
    source ${CONTAINER_ENV_FILE}
  fi
}
