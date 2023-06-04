DOCKER_USERNAME=${DEFAULT_KAMEHOUSE_USERNAME}
DOCKER_PORT_SSH=6022
DOCKER_PORT_HTTP=6080
DOCKER_PORT_HTTPS=6443
DOCKER_PORT_TOMCAT_DEBUG=6000
DOCKER_PORT_TOMCAT=6090
DOCKER_PORT_MYSQL=6306
IS_LINUX_DOCKER_HOST=""

DOCKER_PROFILES_LIST="(ci|dev|demo|prod|prod-ext)"
DEFAULT_DOCKER_PROFILE="dev"
DOCKER_PROFILE="${DEFAULT_DOCKER_PROFILE}"

DEFAULT_DOCKER_OS="ubuntu"
DOCKER_ENVIRONMENT="${DEFAULT_DOCKER_OS}"

DOCKER_COMMAND=""
# When I update the base image here also update docker-setup.md
DOCKER_IMAGE_BASE="ubuntu:22.04"
DOCKER_IMAGE_TAG="latest"

# This may not give me the correct host ip address if there's another adapter with address 172.xxx.xxx.xxx
DOCKER_HOST_DEFAULT_SUBNET="172\.[0-9]\+\.[0-9]\+\.[0-9]\+"
#DOCKER_HOST_DEFAULT_SUBNET="192\.168\.56\.[0-9]\+"

# Get the ip address of the host running kamehouse in a docker container
getKameHouseDockerHostIp() {
  local DOCKER_HOST_SUBNET=$1
  if [ -z "${DOCKER_HOST_SUBNET}" ]; then
    DOCKER_HOST_SUBNET=${DOCKER_HOST_DEFAULT_SUBNET}
  fi

  if ${IS_LINUX_HOST}; then
    echo `ifconfig docker0 | grep -e "${DOCKER_HOST_SUBNET}" | grep "inet" | awk '{print $2}'`
  else
    echo `ipconfig | grep -e "${DOCKER_HOST_SUBNET}" | grep "IPv4" | awk '{print $14}'`
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
    DOCKER_PORT_HTTP=15080
    DOCKER_PORT_SSH=15022
  fi

  if [ "${DOCKER_PROFILE}" == "demo" ]; then
    DOCKER_PORT_HTTP=12080
    DOCKER_PORT_SSH=12022
  fi

  if [ "${DOCKER_PROFILE}" == "dev" ]; then
    DOCKER_PORT_HTTP=6080
    DOCKER_PORT_SSH=6022
  fi

  if [ "${DOCKER_PROFILE}" == "prod" ]; then
    DOCKER_PORT_HTTP=7080
    DOCKER_PORT_SSH=7022
  fi

  if [ "${DOCKER_PROFILE}" == "prod-ext" ]; then
    DOCKER_PORT_HTTP=7080
    DOCKER_PORT_SSH=7022
  fi  
}

printDockerProfileOption() {
  addHelpOption "-p ${DOCKER_PROFILES_LIST}" "default profile is ${DEFAULT_DOCKER_PROFILE}"
}
