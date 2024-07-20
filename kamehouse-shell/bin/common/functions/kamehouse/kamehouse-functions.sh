export PATH="${HOME}/programs/apache-maven/bin:${PATH}"

# Common kamehouse variables
# DEFAULT_KAMEHOUSE_USERNAME gets set during install kamehouse-shell
DEFAULT_KAMEHOUSE_USERNAME=""

GIT_BASH="%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat"
PROJECT="kamehouse"
PROJECT_DIR="${HOME}/git/${PROJECT}"
USE_CURRENT_DIR=false
DEFAULT_SSH_USER=${DEFAULT_KAMEHOUSE_USERNAME}
SSH_USER=${DEFAULT_SSH_USER}
SSH_COMMAND=""
SSH_SERVER=""
SSH_OUTPUT=""
SSH_EXIT_CODE=""
SSH_PORT=22
GIT_COMMIT_HASH=
SUDO_KAMEHOUSE_COMMAND=""

MODULES_LIST="(admin|cmd|groot|media|mobile|shell|tennisworld|testmodule|ui|vlcrc)"
MODULE_SHORT=""
MODULE=""

MAVEN_PROFILES_LIST="(prod|qa|dev|docker|ci)"
DEFAULT_MAVEN_PROFILE="prod"
MAVEN_PROFILE="${DEFAULT_MAVEN_PROFILE}"

KAMEHOUSE_SERVERS_LIST="(docker|local|niko-nba|niko-server|niko-server-vm-ubuntu|niko-w|niko-w-vm-ubuntu|pi)"
DEFAULT_KAMEHOUSE_SERVER="local"
KAMEHOUSE_SERVER="${DEFAULT_KAMEHOUSE_SERVER}"

TOMCAT_DIR="${HOME}/programs/apache-tomcat"
TOMCAT_DIR_DEV="${HOME}/programs/apache-tomcat-dev"

DEFAULT_TOMCAT_PORT=9090
TOMCAT_PORT=${DEFAULT_TOMCAT_PORT}
TOMCAT_DEBUG_PORT=8000
DEFAULT_TOMCAT_DEV_PORT=9980

DEFAULT_HTTPD_PORT=443
HTTPD_PORT=${DEFAULT_HTTPD_PORT}

KAMEHOUSE_BUILD_VERSION=""
KAMEHOUSE_CMD_DEPLOY_PATH="${HOME}/programs"

# docker defaults
IS_DOCKER_CONTAINER=false
IS_REMOTE_LINUX_HOST=false

CONTAINER_ENV_FILE="${HOME}/.kamehouse/.kamehouse-docker-container-env"

# Generic username and password command line arguments
USERNAME_ARG=""
PASSWORD_ARG=""

# buildMavenCommand defaults 
MAVEN_COMMAND=
INTEGRATION_TESTS=false
CONTINUE_INTEGRATION_TESTS_ON_ERRORS=false
RESUME_BUILD=false
FAST_BUILD=false

# buildMobile defaults
KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN="/d/Downloads/Google Drive/KameHouse/kamehouse-mobile"
KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN="${HOME}/GoogleDrive/KameHouse/kamehouse-mobile"
KAMEHOUSE_ANDROID_APK="/kamehouse-mobile/platforms/android/app/build/outputs/apk/debug/app-debug.apk"
KAMEHOUSE_ANDROID_APK_PATH=""

KAMEHOUSE_MOBILE_APP_SERVER="pi"
KAMEHOUSE_MOBILE_APP_USER="pi"
KAMEHOUSE_MOBILE_APP_PATH="/var/www/kamehouse-webserver/kame-house-mobile"

# ---------------------------
# Common kamehouse functions
# ---------------------------

parseHttpdPort() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -p)
        HTTPD_PORT="${ARGS[i+1]}"
        ;;
    esac
  done
}

parseKameHouseModule() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -m)
        MODULE_SHORT="${ARGS[i+1]}"
        MODULE="kamehouse-${MODULE_SHORT}"
        ;;
    esac
  done
}

parseKameHouseServer() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -z)
        KAMEHOUSE_SERVER="${ARGS[i+1]}"
        ;;
    esac
  done
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

parsePasswordArg() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -p)
        PASSWORD_ARG="${ARGS[i+1]}"
        ;;
    esac
  done
}

parseTomcatPort() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -p)
        TOMCAT_PORT="${ARGS[i+1]}"
        ;;
    esac
  done
}

parseUsernameArg() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -u)
        USERNAME_ARG="${ARGS[i+1]}"
        ;;
    esac
  done
}

setEnvForHttpdPort() {
  local REGEX_NUMBER='^[0-9]+$'
  if [[ ! ${HTTPD_PORT} =~ $REGEX_NUMBER ]]; then
    log.error "Option -p has an invalid value of ${HTTPD_PORT}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

setEnvForKameHouseModule() {
  if [ -n "${MODULE_SHORT}" ]; then
    if [ "${MODULE_SHORT}" != "admin" ] \
        && [ "${MODULE_SHORT}" != "cmd" ] \
        && [ "${MODULE_SHORT}" != "groot" ] \
        && [ "${MODULE_SHORT}" != "media" ] \
        && [ "${MODULE_SHORT}" != "mobile" ] \
        && [ "${MODULE_SHORT}" != "shell" ] \
        && [ "${MODULE_SHORT}" != "tennisworld" ] \
        && [ "${MODULE_SHORT}" != "testmodule" ] \
        && [ "${MODULE_SHORT}" != "ui" ] \
        && [ "${MODULE_SHORT}" != "vlcrc" ]; then
      log.error "Option -m module needs to be in ${MODULES_LIST}"
      printHelp
      exitProcess ${EXIT_INVALID_ARG}
    fi
  fi
}

setEnvForKameHouseServer() {
  SSH_SERVER=${KAMEHOUSE_SERVER}
  KAMEHOUSE_SERVER=$(echo "${KAMEHOUSE_SERVER}" | tr '[:upper:]' '[:lower:]')

  if [ "${KAMEHOUSE_SERVER}" != "docker" ] &&
    [ "${KAMEHOUSE_SERVER}" != "local" ] &&
    [ "${KAMEHOUSE_SERVER}" != "niko-nba" ] &&
    [ "${KAMEHOUSE_SERVER}" != "niko-server" ] &&
    [ "${KAMEHOUSE_SERVER}" != "niko-server-vm-ubuntu" ] &&
    [ "${KAMEHOUSE_SERVER}" != "niko-w" ] &&
    [ "${KAMEHOUSE_SERVER}" != "niko-w-vm-ubuntu" ] &&
    [ "${KAMEHOUSE_SERVER}" != "pi" ]; then
    log.error "Option -z server has an invalid value of ${KAMEHOUSE_SERVER}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
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

setEnvForMavenProfile() {
  MAVEN_PROFILE=`echo "${MAVEN_PROFILE}" | tr '[:upper:]' '[:lower:]'`
  
  if [ "${MAVEN_PROFILE}" != "prod" ] \
      && [ "${MAVEN_PROFILE}" != "qa" ] \
      && [ "${MAVEN_PROFILE}" != "dev" ] \
      && [ "${MAVEN_PROFILE}" != "docker" ] \
      && [ "${MAVEN_PROFILE}" != "ci" ]; then
    log.error "Option -p profile needs to be in ${MAVEN_PROFILES_LIST}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

setEnvForTomcatPort() {
  local REGEX_NUMBER='^[0-9]+$'
  if [[ ! ${TOMCAT_PORT} =~ $REGEX_NUMBER ]]; then
    log.error "Option -p has an invalid value of ${TOMCAT_PORT}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

printHttpdPortOption() {
  addHelpOption "-p" "httpd port. Default ${DEFAULT_HTTPD_PORT}"
}

printKameHouseModuleOption() {
  local OPERATION=$1
  addHelpOption "-m ${MODULES_LIST}" "module to ${OPERATION}"
}

printKameHouseServerOption() {
  addHelpOption "-z ${KAMEHOUSE_SERVERS_LIST}" "server to execute script on. Default is ${DEFAULT_KAMEHOUSE_SERVER}"
}

printMavenProfileOption() {
  addHelpOption "-p ${MAVEN_PROFILES_LIST}" "maven profile to build the project with. Default is ${DEFAULT_MAVEN_PROFILE} if not specified"
}

printPasswordArgOption() {
  addHelpOption "-p" "password"
}

printTomcatPortOption() {
  addHelpOption "-p" "tomcat port. Default ${DEFAULT_TOMCAT_PORT}"
}

printUsernameArgOption() {
  addHelpOption "-u" "username"
}

# Executes the SSH_COMMAND in the remote SSH_SERVER as the user SSH_USER
executeSshCommand() {
  local SKIP_EXIT_CODE_CHECK=$1
  log.info "Executing '${COL_PURPLE}${SSH_COMMAND}${COL_DEFAULT_LOG}' in remote server ${COL_PURPLE}${SSH_SERVER}${COL_DEFAULT_LOG}"
  if ${IS_REMOTE_LINUX_HOST}; then
    SSH_COMMAND="source ~/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh ; "${SSH_COMMAND}
    log.debug "ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER} -C \"${SSH_COMMAND}\""
    SSH_OUTPUT=`ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER} -C "${SSH_COMMAND}"`
    SSH_EXIT_CODE=$?
  else
    log.debug "ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER} \"${GIT_BASH} -c \\\"${SSH_COMMAND}\\\"\""
    SSH_OUTPUT=`ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER} "${GIT_BASH} -c \"${SSH_COMMAND}\""`
    SSH_EXIT_CODE=$?
  fi
  log.info "Ssh ${SSH_USER}@${SSH_SERVER} command output ${COL_PURPLE}start"
  echo "${SSH_OUTPUT}"
  log.info "Ssh ${SSH_USER}@${SSH_SERVER} command output ${COL_PURPLE}end"
  if ${SKIP_EXIT_CODE_CHECK}; then
    log.debug "Skipping ssh command exit code check"
  else
    checkCommandStatus "${SSH_EXIT_CODE}" "An error occurred while executing '${SSH_COMMAND}' in remote server ${SSH_SERVER}"
  fi
  log.info "Finished executing '${COL_PURPLE}${SSH_COMMAND}${COL_DEFAULT_LOG}' in remote server ${COL_PURPLE}${SSH_SERVER}${COL_DEFAULT_LOG}"
}

executeOperationInTomcatManager() {
  local OPERATION=$1
  local TOMCAT_PORT=$2
  local KAMEHOUSE_MODULE=$3

  if [ -z "${KAMEHOUSE_MODULE}" ]; then
    log.info "Executing ${COL_PURPLE}${OPERATION}${COL_DEFAULT_LOG} kamehouse webapps in localhost:${TOMCAT_PORT} for all modules"
  else
    log.info "Executing ${COL_PURPLE}${OPERATION}${COL_DEFAULT_LOG} kamehouse webapps in localhost:${TOMCAT_PORT} for module ${COL_PURPLE}${KAMEHOUSE_MODULE}${COL_DEFAULT_LOG}"
  fi

  local WEBAPP=${KAMEHOUSE_MODULE}
  if [ "${KAMEHOUSE_MODULE}" == "ui" ]; then
    WEBAPP=""
  fi

  local URL_LIST="http://${TOMCAT_TEXT_USER}:${TOMCAT_TEXT_PASS}@localhost:${TOMCAT_PORT}/manager/text/list"
  log.debug "curl url: ${URL_LIST}"
  local KAMEHOUSE_WEBAPPS=`curl "${URL_LIST}" 2>/dev/null | grep "/kame-house" | grep "${WEBAPP}" | awk -F':' '{print $1}'`
  
  if [ -z "${KAMEHOUSE_WEBAPPS}" ]; then
    log.warn "Tomcat doesn't seem to be running. Nothing to do. Exiting without executing ${COL_PURPLE}${OPERATION}"
    return
  fi
  
  if [ "${KAMEHOUSE_MODULE}" == "ui" ]; then
    KAMEHOUSE_WEBAPPS="/kame-house"
  fi

  echo -e "${KAMEHOUSE_WEBAPPS}" | while read KAMEHOUSE_WEBAPP; do
    log.info "Executing ${COL_PURPLE}${OPERATION} ${KAMEHOUSE_WEBAPP}${COL_DEFAULT_LOG} in localhost:${TOMCAT_PORT}"
    local URL_OPERATION="http://${TOMCAT_TEXT_USER}:${TOMCAT_TEXT_PASS}@localhost:${TOMCAT_PORT}/manager/text/${OPERATION}?path=${KAMEHOUSE_WEBAPP}"
    log.debug "curl url: ${URL_OPERATION}"
    curl "${URL_OPERATION}" 2>/dev/null
    sleep 2
  done
}

# Loads the environment variables set when running in a docker container
# Look at the docker-init script to see what variables are set in the container env
loadDockerContainerEnv() {
  if [ -f "${CONTAINER_ENV_FILE}" ]; then
    log.debug "Running inside a docker container"
    source ${CONTAINER_ENV_FILE}
  fi
}

# Set sudo for a command that requires sudo
setSudoKameHouseCommand() {
  SUDO_KAMEHOUSE_COMMAND=$1
  if ${IS_LINUX_HOST}; then
    log.info "${COL_PURPLE}'${SUDO_KAMEHOUSE_COMMAND}'${COL_DEFAULT_LOG} needs to run with ${COL_RED}sudo${COL_DEFAULT_LOG}. Running as user ${COL_RED}${USER}${COL_DEFAULT_LOG}"
    if ! ${IS_ROOT_USER}; then
      SUDO_KAMEHOUSE_COMMAND="sudo ${SUDO_KAMEHOUSE_COMMAND}"
    fi
  fi
  log.debug "${SUDO_KAMEHOUSE_COMMAND}"
}

# Get kamehouse httpd content root directory
getHttpdContentRoot() {
  if ${IS_LINUX_HOST}; then
    echo "/var/www/kamehouse-webserver"  
  else
    echo "${HOME}/programs/apache-httpd/www/kamehouse-webserver"
  fi
}

setKameHouseRootProjectDir() {
  log.debug "Setting kamehouse project root directory to use"
  if ${USE_CURRENT_DIR}; then
    PROJECT_DIR=`pwd`
  else  
    cd ${PROJECT_DIR}
    checkCommandStatus "$?" "Can't cd to ${PROJECT_DIR}. Invalid kamehouse project root directory" 
  fi
  checkValidRootKameHouseProject
  log.info "Using kamehouse project root directory: ${COL_PURPLE}${PROJECT_DIR}"
}

checkValidRootKameHouseProject() {
  if [ ! -d "./kamehouse-shell/bin" ] || [ ! -d "./.git" ]; then
    log.error "Invalid kamehouse project root directory: `pwd`"
    exitProcess ${EXIT_ERROR}
  fi
}

pullLatestKameHouseChanges() {
  if ${USE_CURRENT_DIR}; then
    log.trace "Using USE_CURRENT_DIR so skipping git pull kamehouse"
    return
  fi
  if [ "${IS_DOCKER_CONTAINER}" == "true" ] && [ "${DOCKER_PROFILE}" == "dev" ]; then
    log.info "Running on a dev docker container. Skipping reset git branch"
    return
  fi
  log.info "Pulling latest version of dev branch of ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} from repository"     
  git checkout dev
  checkCommandStatus "$?" "An error occurred checking out dev branch"
  
  git reset --hard

  git pull origin dev
  checkCommandStatus "$?" "An error occurred pulling origin dev"
}

cleanLogsInGitRepoFolder() {
  log.debug "Clearing logs in git repo folder"
  rm -v -f logs/*.log

  local KAMEHOUSE_MODULES=`ls -1 | grep kamehouse-`
  echo -e "${KAMEHOUSE_MODULES}" | while read KAMEHOUSE_MODULE; do
    rm -v -f ${KAMEHOUSE_MODULE}/logs/*.log
  done
}

cleanUpMavenRepository() {
  log.info "Removing com.nicobrest entries from ${HOME}/.m2"
  rm -rf ${HOME}/.m2/repository/com/nicobrest
}

cdToKameHouseModule() {
  local KAMEHOUSE_MODULE=$1
  checkValidRootKameHouseProject
  cd ${KAMEHOUSE_MODULE}
  checkCommandStatus "$?" "Error cd to ${KAMEHOUSE_MODULE}. Are you running the script from the root of kamehouse project?"
}

cdToRootDirFromModule() {
  local KAMEHOUSE_MODULE=$1
  local CURRENT_DIR=`basename $(pwd)`
  if [ "${CURRENT_DIR}" == "${KAMEHOUSE_MODULE}" ]; then
    cd ..
  fi
  checkValidRootKameHouseProject
}

# runs from kamehouse-mobile directory
setKameHouseMobileApkPath() {
  KAMEHOUSE_ANDROID_APK_PATH=${PROJECT_DIR}${KAMEHOUSE_ANDROID_APK}
  log.debug "Setting KAMEHOUSE_ANDROID_APK_PATH=${KAMEHOUSE_ANDROID_APK_PATH}"
}
