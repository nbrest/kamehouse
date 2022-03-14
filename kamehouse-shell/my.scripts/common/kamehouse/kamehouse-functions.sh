# Common kamehouse variables
PROJECT="java.web.kamehouse"
DEFAULT_SSH_USER=nbrest
SSH_USER=${DEFAULT_SSH_USER}
SSH_COMMAND=""
SSH_SERVER=""
SSH_PORT=22
AWS_SSH_SERVER="ec2-13-211-209-87.ap-southeast-2.compute.amazonaws.com"
AWS_SSH_USER=ubuntu

TOMCAT_PORT=9090
TOMCAT_DEBUG_PORT=8000

IS_DOCKER_CONTAINER=false
IS_REMOTE_LINUX_HOST=false
ENVIRONMENT=""

DOCKER_USERNAME=nbrest
DOCKER_PORT_SSH=6022
DOCKER_PORT_HTTP=6080
DOCKER_PORT_HTTPS=6443
DOCKER_PORT_TOMCAT_DEBUG=6000
DOCKER_PORT_TOMCAT=6090
DOCKER_PORT_MYSQL=6306
IS_LINUX_DOCKER_HOST=""

CONTAINER_ENV_FILE="${HOME}/.kamehouse/.kamehouse-docker-container-env"

# This may not give me the correct host ip address if there's another adapter with address 172.xxx.xxx.xxx
DOCKER_HOST_DEFAULT_SUBNET="172\.[0-9]\+\.[0-9]\+\.[0-9]\+"

# Common kamehouse functions
parseEnvironment() {
  local ENV_ARG=$1
  ENV_ARG=$(echo "${ENV_ARG}" | tr '[:upper:]' '[:lower:]')

  if [ "${ENV_ARG}" != "aws" ] &&
    [ "${ENV_ARG}" != "docker" ] &&
    [ "${ENV_ARG}" != "local" ] &&
    [ "${ENV_ARG}" != "niko-nba" ] &&
    [ "${ENV_ARG}" != "niko-server" ] &&
    [ "${ENV_ARG}" != "niko-server-vm-ubuntu" ] &&
    [ "${ENV_ARG}" != "niko-w" ] &&
    [ "${ENV_ARG}" != "niko-w-vm-ubuntu" ]; then
    log.error "Option -e environment has an invalid value of ${ENV_ARG}"
    printHelp
    exitProcess 1
  fi

  ENVIRONMENT=${ENV_ARG}
  case ${ENVIRONMENT} in
  "aws")
    IS_REMOTE_LINUX_HOST=true
    ;;
  "local") ;;
  "niko-nba")
    IS_REMOTE_LINUX_HOST=false
    ;;
  "niko-server")
    IS_REMOTE_LINUX_HOST=false
    ;;
  "niko-server-vm-ubuntu")
    IS_REMOTE_LINUX_HOST=true
    ;;  
  "niko-w")
    IS_REMOTE_LINUX_HOST=false
    ;;
  "niko-w-vm-ubuntu")
    IS_REMOTE_LINUX_HOST=true
    ;;
  esac
}

# Executes the SSH_COMMAND in the remote SSH_SERVER as the user SSH_USER
executeSshCommand() {
  log.info "Executing '${COL_PURPLE}${SSH_COMMAND}${COL_DEFAULT_LOG}' in remote server ${COL_PURPLE}${SSH_SERVER}${COL_DEFAULT_LOG}"
  if ${IS_REMOTE_LINUX_HOST}; then
    ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER} -C "${SSH_COMMAND}"
  else
    # This command depends on having git-bash.bat from my.scripts repo in my PATH in the SSH_SERVER
    ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER} "git-bash -c \"\"${SSH_COMMAND}\"\""
  fi
  checkCommandStatus "$?" "An error occurred while executing '${SSH_COMMAND}' in remote server ${SSH_SERVER}"
  log.info "Finished executing '${COL_PURPLE}${SSH_COMMAND}${COL_DEFAULT_LOG}' in remote server ${COL_PURPLE}${SSH_SERVER}${COL_DEFAULT_LOG}"
}

cleanLogsInGitRepoFolder() {
  log.info "Clearing logs in git repo folder"
  rm -v -f logs/*.log

  local KAMEHOUSE_MODULES=`ls -1 | grep kamehouse-`
  echo -e "${KAMEHOUSE_MODULES}" | while read KAMEHOUSE_MODULE; do
    rm -v -f ${KAMEHOUSE_MODULE}/logs/*.log
  done
}

executeOperationInTomcatManager() {
  local OPERATION=$1
  local TOMCAT_PORT=$2
  local KAMEHOUSE_MODULE=$3
  log.info "Executing ${OPERATION} kamehouse webapps in localhost:${TOMCAT_PORT} for module '${KAMEHOUSE_MODULE}'"
  local WEBAPP=${KAMEHOUSE_MODULE}
  if [ "${KAMEHOUSE_MODULE}" == "ui" ]; then
    WEBAPP=""
  fi

  local URL_LIST="http://${TOMCAT_TEXT_USER}:${TOMCAT_TEXT_PASS}@localhost:${TOMCAT_PORT}/manager/text/list"
  #log.trace "curl url: ${URL_LIST}"
  local KAMEHOUSE_WEBAPPS=`curl "${URL_LIST}" 2>/dev/null | grep "/kame-house" | grep "${WEBAPP}" | awk -F':' '{print $1}'`
  
  if [ "${KAMEHOUSE_MODULE}" == "ui" ]; then
    KAMEHOUSE_WEBAPPS="/kame-house"
  fi
  
  if [ -z "${KAMEHOUSE_WEBAPPS}" ]; then
    log.warn "KAMEHOUSE_WEBAPPS is empty. Nothing to do. Exiting without executing ${OPERATION}"
    return
  fi

  echo -e "${KAMEHOUSE_WEBAPPS}" | while read KAMEHOUSE_WEBAPP; do
    log.info "Executing ${OPERATION} ${KAMEHOUSE_WEBAPP} in localhost:${TOMCAT_PORT}"
    local URL_OPERATION="http://${TOMCAT_TEXT_USER}:${TOMCAT_TEXT_PASS}@localhost:${TOMCAT_PORT}/manager/text/${OPERATION}?path=${KAMEHOUSE_WEBAPP}"
    #log.trace "curl url: ${URL_OPERATION}"
    curl "${URL_OPERATION}" 2>/dev/null
    sleep 2
  done
}

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

# Loads the environment variables set when running in a docker container
# Look at the docker-init script to see what variables are set in the container env
loadDockerContainerEnv() {
  if [ -f "${CONTAINER_ENV_FILE}" ]; then
    #log.debug "Running inside a docker container"
    source ${CONTAINER_ENV_FILE}
  fi
}

# Assumes it's running on the root of the git kamehouse project
exportGitCommitHash() {
  log.info "Exporting git commit hash to project"
  git rev-parse --short HEAD > kamehouse-commons-core/src/main/resources/git-commit-hash.txt
}