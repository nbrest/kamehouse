# Common kamehouse variables
# DEFAULT_KAMEHOUSE_USERNAME gets set during install kamehouse-shell
DEFAULT_KAMEHOUSE_USERNAME=""
GIT_BASH="%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat"
PROJECT="kamehouse"
DEFAULT_SSH_USER=${DEFAULT_KAMEHOUSE_USERNAME}
SSH_USER=${DEFAULT_SSH_USER}
SSH_COMMAND=""
SSH_SERVER=""
SSH_PORT=22
GIT_COMMIT_HASH=
SUDO_KAMEHOUSE_COMMAND=""

KAMEHOUSE_SERVERS_LIST="(docker|local|niko-nba|niko-server|niko-server-vm-ubuntu|niko-w|niko-w-vm-ubuntu|pi)"
DEFAULT_KAMEHOUSE_SERVER="local"
KAMEHOUSE_SERVER="${DEFAULT_KAMEHOUSE_SERVER}"

TOMCAT_MODULES_LIST="(admin|media|tennisworld|testmodule|ui|vlcrc)"
MODULES_LIST="(admin|cmd|groot|media|mobile|shell|tennisworld|testmodule|ui|vlcrc)"

MAVEN_PROFILES_LIST="(prod|qa|dev|docker|ci)"
DEFAULT_MAVEN_PROFILE="prod"
MAVEN_PROFILE="${DEFAULT_MAVEN_PROFILE}"

TOMCAT_PORT=9090
TOMCAT_DEBUG_PORT=8000

IS_DOCKER_CONTAINER=false
IS_REMOTE_LINUX_HOST=false

CONTAINER_ENV_FILE="${HOME}/.kamehouse/.kamehouse-docker-container-env"

# Common kamehouse functions

parseKameHouseServer() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -e) # TODO move to -s
        KAMEHOUSE_SERVER="${ARGS[i+1]}"
        ;;
    esac
  done
}

setEnvForKameHouseServer() {
  KAMEHOUSE_SERVER=$(echo "${KAMEHOUSE_SERVER}" | tr '[:upper:]' '[:lower:]')

  if [ "${KAMEHOUSE_SERVER}" != "docker" ] &&
    [ "${KAMEHOUSE_SERVER}" != "local" ] &&
    [ "${KAMEHOUSE_SERVER}" != "niko-nba" ] &&
    [ "${KAMEHOUSE_SERVER}" != "niko-server" ] &&
    [ "${KAMEHOUSE_SERVER}" != "niko-server-vm-ubuntu" ] &&
    [ "${KAMEHOUSE_SERVER}" != "niko-w" ] &&
    [ "${KAMEHOUSE_SERVER}" != "niko-w-vm-ubuntu" ] &&
    [ "${KAMEHOUSE_SERVER}" != "pi" ]; then
    log.error "Option -e server has an invalid value of ${KAMEHOUSE_SERVER}"
    printHelp
    exitProcess 1
  fi

  case ${KAMEHOUSE_SERVER} in
  "docker")
    IS_REMOTE_LINUX_HOST=true
    SSH_USER=${DEFAULT_SSH_USER}
    ;;
  "local") ;;
  "niko-nba")
    IS_REMOTE_LINUX_HOST=false
    SSH_USER=nbrest
    ;;
  "niko-server")
    IS_REMOTE_LINUX_HOST=false
    SSH_USER=nbrest
    ;;
  "niko-server-vm-ubuntu")
    IS_REMOTE_LINUX_HOST=true
    SSH_USER=nbrest
    ;;  
  "niko-w")
    IS_REMOTE_LINUX_HOST=false
    SSH_USER=nbrest
    ;;
  "niko-w-vm-ubuntu")
    IS_REMOTE_LINUX_HOST=true
    SSH_USER=nbrest
    ;;
  "pi")
    IS_REMOTE_LINUX_HOST=true
    SSH_USER=pi
    ;;
  esac
}

printKameHouseServerOption() {
  addHelpOption "-e ${KAMEHOUSE_SERVERS_LIST}" "server to execute script on. Default is ${DEFAULT_KAMEHOUSE_SERVER}"
}

parseMavenProfile() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -p)
        MAVEN_PROFILE="${ARGS[i+1]}"
        ;;
    esac
  done
}

setEnvForMavenProfile() {
  MAVEN_PROFILE=`echo "${MAVEN_PROFILE}" | tr '[:upper:]' '[:lower:]'`
  
  if [ "${MAVEN_PROFILE}" != "prod" ] \
      && [ "${MAVEN_PROFILE}" != "qa" ] \
      && [ "${MAVEN_PROFILE}" != "dev" ] \
      && [ "${MAVEN_PROFILE}" != "docker" ] \
      && [ "${MAVEN_PROFILE}" != "ci" ]; then
    log.error "Option -p profile needs to be in ${MAVEN_PROFILES_LIST}"
    printHelp
    exitProcess 1
  fi
}

printMavenProfileOption() {
  addHelpOption "-p ${MAVEN_PROFILES_LIST}" "maven profile to build the project with. Default is ${DEFAULT_MAVEN_PROFILE} if not specified"
}

# Executes the SSH_COMMAND in the remote SSH_SERVER as the user SSH_USER
executeSshCommand() {
  log.info "Executing '${COL_PURPLE}${SSH_COMMAND}${COL_DEFAULT_LOG}' in remote server ${COL_PURPLE}${SSH_SERVER}${COL_DEFAULT_LOG}"
  if ${IS_REMOTE_LINUX_HOST}; then
    SSH_COMMAND="source \${HOME}/programs/kamehouse-shell/bin/lin/bashrc/bashrc.sh ; "${SSH_COMMAND}
    log.debug "ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER} -C \"${SSH_COMMAND}\""
    ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER} -C "${SSH_COMMAND}"
  else
    log.debug "ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER} \"${GIT_BASH} -c \\\"${SSH_COMMAND}\\\"\""
    ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER} "${GIT_BASH} -c \"${SSH_COMMAND}\""
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
  log.debug "curl url: ${URL_LIST}"
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
    log.debug "curl url: ${URL_OPERATION}"
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
    log.debug "Running inside a docker container"
    source ${CONTAINER_ENV_FILE}
  fi
}

# Assumes it's running on the root of the git kamehouse project
exportGitCommitHash() {
  log.info "Exporting git commit hash to project"
  GIT_COMMIT_HASH=`git rev-parse --short HEAD`
  echo "${GIT_COMMIT_HASH}" > kamehouse-commons-core/src/main/resources/git-commit-hash.txt
}

# Set a kamehouse command to execute through exec-script.sh or sudo or as root
setSudoKameHouseCommand() {
  log.warn "This script needs to run as ${COL_RED}root${COL_DEFAULT_LOG} or with ${COL_RED}sudo${COL_DEFAULT_LOG} or with ${COL_RED}exec-script.sh${COL_DEFAULT_LOG}"  
  SUDO_KAMEHOUSE_COMMAND=$1
  if ! ${IS_ROOT_USER}; then
    SUDO_KAMEHOUSE_COMMAND="sudo ${SUDO_KAMEHOUSE_COMMAND}"
  fi
  log.debug "${SUDO_KAMEHOUSE_COMMAND}"
}