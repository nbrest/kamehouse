source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

export PATH="${HOME}/programs/apache-maven/bin:${PATH}"

# Common kamehouse variables
# DEFAULT_KAMEHOUSE_USERNAME gets set during install kamehouse-shell
DEFAULT_KAMEHOUSE_USERNAME=""

KAMEHOUSE_CFG="${HOME}/.kamehouse/config/kamehouse.cfg"
WIN_BASH="%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/bash.bat"
PROJECT="kamehouse"
PROJECT_DIR="${HOME}/git/${PROJECT}"
USE_CURRENT_DIR=false
DEFAULT_SSH_USER=${DEFAULT_KAMEHOUSE_USERNAME}
SSH_OPTIONS="-t -o ServerAliveInterval=10"
SSH_PORT=22
SSH_USER=${DEFAULT_SSH_USER}
SSH_SERVER=""
SSH_COMMAND=""
SSH_OUTPUT=""
SSH_EXIT_CODE=""
SCP_OPTIONS="-v"
SCP_SRC=""
SCP_DEST=""
SCP_EXIT_CODE=""
SFTP_OPTIONS="-v"
SFTP_REDIRECT_STD_ERR="2>&1"
SFTP_PORT="22" 
SFTP_USER=""
SFTP_SERVER=""
SFTP_COMMAND=""
SFTP_EXIT_CODE=""
GIT_COMMIT_HASH=
SUDO_KAMEHOUSE_COMMAND=""

WIN_USER_HOME="C:\\Users\\${USER}"
KAMEHOUSE_SHELL_PS1_PATH="${WIN_USER_HOME}\\programs\\kamehouse-shell\\bin\\win\\ps1"

MODULES_LIST="(admin|auth|cmd|desktop|groot|media|mobile|shell|snape|tennisworld|testmodule|ui|vlcrc)"
MODULE_SHORT=""
MODULE=""

MAVEN_PROFILES_LIST="(prod|qa|dev|docker|ci)"
DEFAULT_MAVEN_PROFILE="prod"
MAVEN_PROFILE="${DEFAULT_MAVEN_PROFILE}"

KAMEHOUSE_SERVER=""

TOMCAT_MODULES="admin,auth,media,tennisworld,testmodule,vlcrc"
TOMCAT_DIR="${HOME}/programs/apache-tomcat"
TOMCAT_DIR_DEV="${HOME}/programs/apache-tomcat-dev"

DEFAULT_TOMCAT_PORT=9090
TOMCAT_PORT=${DEFAULT_TOMCAT_PORT}
TOMCAT_DEBUG_PORT=8000
DEFAULT_TOMCAT_DEV_PORT=9980
CMD_LINE_DEBUG_PORT=5001

DEFAULT_HTTPD_PORT=443
HTTPD_PORT=${DEFAULT_HTTPD_PORT}

KAMEHOUSE_BUILD_VERSION=""
KAMEHOUSE_CMD_DEPLOY_PATH="${HOME}/programs"

# docker defaults
IS_DOCKER_CONTAINER=false
IS_REMOTE_LINUX_HOST=false

# Generic username and password command line arguments
USERNAME_ARG=""
PASSWORD_ARG=""

# buildMavenCommand defaults 
MAVEN_COMMAND=
INTEGRATION_TESTS=false
CI_BUILD=false
CONTINUE_INTEGRATION_TESTS_ON_ERRORS=false
RESUME_BUILD=false
FAST_BUILD=false

# buildMobile defaults
KAMEHOUSE_ANDROID_APK="/kamehouse-mobile/platforms/android/app/build/outputs/apk/debug/app-debug.apk"
KAMEHOUSE_ANDROID_APK_PATH=""

# Override LOAD_KAMEHOUSE_SECRETS variable in the function initKameHouseShellEnv in the shell scripts
LOAD_KAMEHOUSE_SECRETS=false
KAMEHOUSE_SECRETS_LOADED=false

SNAPE_PATH="${HOME}/programs/kamehouse-snape/bin"

# ---------------------------
# Common kamehouse functions
# ---------------------------
loadKamehouseCfg() {
  source "${HOME}/programs/kamehouse-shell/conf/default-kamehouse.cfg"
  if [ ! -f "${KAMEHOUSE_CFG}" ]; then
    log.debug "${KAMEHOUSE_CFG} not found. Using default values"
    return
  fi
  source ${KAMEHOUSE_CFG}
  if [ "$?" == "0" ]; then
    log.trace "Loaded ${KAMEHOUSE_CFG}"
  else
    log.error "Error importing ${KAMEHOUSE_CFG}. Using default values"
  fi
}

# Loads the environment variables set when running in a docker container
# Look at the docker-init script to see what variables are set in the container env
loadDockerContainerEnv() {
  local CONTAINER_ENV_FILE=".kamehouse/config/.kamehouse-docker-container-env"
  if [ ! -f "${HOME}/${CONTAINER_ENV_FILE}" ]; then
    log.trace "No ~/${CONTAINER_ENV_FILE}. Running outside a docker container"
    return
  fi
  source ${HOME}/${CONTAINER_ENV_FILE}
  if [ "$?" != "0" ]; then
    log.error "Error importing ~/${CONTAINER_ENV_FILE}"
    exit 99
  fi
  log.trace "Loaded ~/${CONTAINER_ENV_FILE}. Running inside a docker container"
}

loadKamehouseSecrets() {
  if [ ! -f "${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc" ]; then
    log.error "Can't find .kamehouse-secrets.cfg.enc file"
    exit 99
  fi  
  local SUFFIX=$RANDOM
  openssl pkeyutl -decrypt -inkey ${HOME}/.kamehouse/config/keys/kamehouse.key -in ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.enc -out ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.tmp.${SUFFIX}
  openssl enc -d -in ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc -out ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.tmp.${SUFFIX} -pbkdf2 -aes256 -kfile ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.tmp.${SUFFIX}
  if [ ! -s "${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.tmp.${SUFFIX}" ]; then
    log.error "Decrypted .kamehouse-secrets.cfg.tmp.${SUFFIX} is empty"
  else
    source ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.tmp.${SUFFIX}
    if [ "$?" == "0" ]; then
      KAMEHOUSE_SECRETS_LOADED=true
      log.trace "Loaded ~/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc"
    fi
  fi  
  rm -f ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.tmp.${SUFFIX} 
  rm -f ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.tmp.${SUFFIX}  
  if ! ${KAMEHOUSE_SECRETS_LOADED}; then
    log.error "Error importing ~/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc"
    exit 99
  fi
}

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
  if [ -z "${MODULE_SHORT}" ]; then
    return
  fi
  if [ "${MODULE_SHORT}" != "admin" ] \
      && [ "${MODULE_SHORT}" != "auth" ] \
      && [ "${MODULE_SHORT}" != "cmd" ] \
      && [ "${MODULE_SHORT}" != "desktop" ] \
      && [ "${MODULE_SHORT}" != "groot" ] \
      && [ "${MODULE_SHORT}" != "media" ] \
      && [ "${MODULE_SHORT}" != "mobile" ] \
      && [ "${MODULE_SHORT}" != "shell" ] \
      && [ "${MODULE_SHORT}" != "snape" ] \
      && [ "${MODULE_SHORT}" != "tennisworld" ] \
      && [ "${MODULE_SHORT}" != "testmodule" ] \
      && [ "${MODULE_SHORT}" != "ui" ] \
      && [ "${MODULE_SHORT}" != "vlcrc" ]; then
    log.error "Option -m module needs to be in ${MODULES_LIST}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

setEnvForKameHouseServer() {
  SSH_SERVER=${KAMEHOUSE_SERVER}
  KAMEHOUSE_SERVER=$(echo "${KAMEHOUSE_SERVER}" | tr '[:upper:]' '[:lower:]')

  local IS_VALID_KAMEHOUSE_SERVER=false
  while read KAMEHOUSE_CONFIG_ENTRY; do
    if [ -n "${KAMEHOUSE_CONFIG_ENTRY}" ]; then
      local KAMEHOUSE_CONFIG_ENTRY_SPLIT=$(echo ${KAMEHOUSE_CONFIG_ENTRY} | tr "," "\n")
      local KAMEHOUSE_CONFIG=()
      while read KAMEHOUSE_CONFIG_ENTRY_FIELD; do
        if [ -n "${KAMEHOUSE_CONFIG_ENTRY_FIELD}" ]; then
          KAMEHOUSE_CONFIG+=("${KAMEHOUSE_CONFIG_ENTRY_FIELD}")
        fi
      done <<< ${KAMEHOUSE_CONFIG_ENTRY_SPLIT}
      local KAMEHOUSE_SERVER_NAME="${KAMEHOUSE_CONFIG[0]}"
      local IS_DOCKER_CONTAINER=false
      if [ "${KAMEHOUSE_CONFIG[5]}" == "--is-docker-container" ]; then
        IS_DOCKER_CONTAINER=true
      fi
      if [ "${KAMEHOUSE_SERVER}" == "${KAMEHOUSE_SERVER_NAME}" ] &&
          [ "${IS_DOCKER_CONTAINER}" == "false" ]; then
        SSH_USER="${KAMEHOUSE_CONFIG[1]}"
        IS_VALID_KAMEHOUSE_SERVER=true        
        IS_REMOTE_LINUX_HOST=false
        if [ "${KAMEHOUSE_CONFIG[6]}" == "--is-linux-host" ]; then
          IS_REMOTE_LINUX_HOST=true
        fi
      fi
    fi
  done <<< ${KAMEHOUSE_SERVER_CONFIGS}   
  if ! ${IS_VALID_KAMEHOUSE_SERVER}; then
    log.error "Option -z server has an invalid value of ${KAMEHOUSE_SERVER}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
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
  addHelpOption "-z ${KAMEHOUSE_SERVERS_LIST}" "server to execute script on" "r"
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

executeSshCommand() {
  local SKIP_EXIT_CODE_CHECK=$1
  local SESSION_ID=$RANDOM
  if ${IS_REMOTE_LINUX_HOST}; then
    SSH_COMMAND="source ~/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh ; "${SSH_COMMAND}
    log.info "SID:${SESSION_ID}: ssh -p ${SSH_PORT} ${SSH_OPTIONS} ${SSH_USER}@${SSH_SERVER} -C \"${SSH_COMMAND}\""
    SSH_OUTPUT=`ssh -p ${SSH_PORT} ${SSH_OPTIONS} ${SSH_USER}@${SSH_SERVER} -C "${SSH_COMMAND}"`
    SSH_EXIT_CODE=$?
  else
    log.info "SID:${SESSION_ID}: ssh -p ${SSH_PORT} ${SSH_OPTIONS} ${SSH_USER}@${SSH_SERVER} \"${WIN_BASH} -c \\\"${SSH_COMMAND}\\\"\""
    SSH_OUTPUT=`ssh -p ${SSH_PORT} ${SSH_OPTIONS} ${SSH_USER}@${SSH_SERVER} "${WIN_BASH} -c \"${SSH_COMMAND}\""`
    SSH_OUTPUT="${COL_YELLOW}  Remote windows server ssh output is not available. Check the logs in the remote server"
    SSH_EXIT_CODE=$?
  fi
  log.info "${COL_CYAN}---------- ${SSH_USER}@${SSH_SERVER} ssh command output start. SID:${SESSION_ID}"
  log.info "${SSH_OUTPUT}" --log-message-only
  log.info "${COL_CYAN}---------- ${SSH_USER}@${SSH_SERVER} ssh command output end. SID:${SESSION_ID}"

  if [ "${SKIP_EXIT_CODE_CHECK}" == "--skip-exit-code-check" ]; then
    log.debug "Skipping ssh command exit code check: ${SSH_EXIT_CODE}"
    return ${SSH_EXIT_CODE}
  fi
  checkCommandStatus "${SSH_EXIT_CODE}" "An error occurred while executing ssh command on ${SSH_SERVER}"
}

executeScpCommand() {
  local SKIP_EXIT_CODE_CHECK=$1
  log.info "scp ${SCP_OPTIONS} ${SCP_SRC} ${SCP_DEST}"
  scp ${SCP_OPTIONS} ${SCP_SRC} ${SCP_DEST}
  SCP_EXIT_CODE=$?
  if [ "${SKIP_EXIT_CODE_CHECK}" == "--skip-exit-code-check" ]; then
    log.debug "Skipping scp command exit code check: ${SCP_EXIT_CODE}"
    return ${SCP_EXIT_CODE}
  fi
  checkCommandStatus "${SCP_EXIT_CODE}" "An error occurred while executing scp command"  
}

executeSftpCommand() {
  local SKIP_EXIT_CODE_CHECK=$1
  log.info "sftp ${SFTP_OPTIONS} -P ${SFTP_PORT} ${SFTP_USER}@${SFTP_SERVER} <<< ${SFTP_COMMAND}"
  sftp ${SFTP_OPTIONS} -P ${SFTP_PORT} ${SFTP_USER}@${SFTP_SERVER} ${SFTP_REDIRECT_STD_ERR} <<< "${SFTP_COMMAND}"
  SFTP_EXIT_CODE=$?
  if [ "${SKIP_EXIT_CODE_CHECK}" == "--skip-exit-code-check" ]; then
    log.debug "Skipping sftp command exit code check: ${SFTP_EXIT_CODE}"
    return ${SFTP_EXIT_CODE}
  fi
  checkCommandStatus "${SFTP_EXIT_CODE}" "An error occurred while executing sftp command on ${SFTP_SERVER}" 
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
  local URL_LIST="http://${TOMCAT_TEXT_USER}:${TOMCAT_TEXT_PASS}@localhost:${TOMCAT_PORT}/manager/text/list"
  local URL_LIST_MASKED="${URL_LIST}"
  URL_LIST_MASKED="`sed 's#://.*:.*@#://****:****@#' <<<"${URL_LIST_MASKED}"`"
  log.debug "curl url: ${URL_LIST_MASKED}"
  local KAMEHOUSE_WEBAPPS=`curl "${URL_LIST}" 2>/dev/null | grep "/kame-house" | grep "${WEBAPP}" | awk -F':' '{print $1}'`
  
  if [ -z "${KAMEHOUSE_WEBAPPS}" ]; then
    log.warn "Tomcat doesn't seem to be running. Nothing to do. Exiting without executing ${COL_PURPLE}${OPERATION}"
    return
  fi

  echo -e "${KAMEHOUSE_WEBAPPS}" | while read KAMEHOUSE_WEBAPP; do
    log.info "Executing ${COL_PURPLE}${OPERATION} ${KAMEHOUSE_WEBAPP}${COL_DEFAULT_LOG} in localhost:${TOMCAT_PORT}"
    local URL_OPERATION="http://${TOMCAT_TEXT_USER}:${TOMCAT_TEXT_PASS}@localhost:${TOMCAT_PORT}/manager/text/${OPERATION}?path=${KAMEHOUSE_WEBAPP}"
    local URL_OPERATION_MASKED="${URL_OPERATION}"
    URL_OPERATION_MASKED="`sed 's#://.*:.*@#://****:****@#' <<<"${URL_OPERATION_MASKED}"`"
    log.debug "curl url: ${URL_OPERATION_MASKED}"
    curl "${URL_OPERATION}" 2>/dev/null
    sleep 2
  done
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
  if [ "$?" != "0" ]; then
    log.error "An error occurred checking out dev branch. Continuing process anyway"
  fi
  
  git reset --hard

  git pull origin dev
  if [ "$?" != "0" ]; then
    log.error "An error occurred pulling dev changes. Continuing process anyway"
  fi
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

# exit the process if keep alive scripts are disabled in the configuration
checkKeepAliveScriptsEnabled() {
  if ${KEEP_ALIVE_SCRIPTS_DISABLED}; then
    log.info "keep alive scripts are disabled in kamehouse.cfg"
    exitProcess ${EXIT_PROCESS_CANCELLED}
  fi
}

# set git commit hash from the git repo in the current directory
setGitCommitHash() {
  GIT_COMMIT_HASH=`git rev-parse HEAD`
  GIT_COMMIT_HASH=`echo ${GIT_COMMIT_HASH:0:9}`
}

# setup environment for scripts running in linux
setupLinuxEnvironment() {
  if ! ${IS_LINUX_HOST}; then
    return
  fi
  log.debug "Setting linux environment"
  local USER_UID=`id -u`

  if [ -z "${TERM}" ]; then
    export TERM=xterm
  fi
  log.debug "TERM=${TERM}"

  if [ -z "${DISPLAY}" ]; then
    export DISPLAY=:0.0
  fi
  log.debug "DISPLAY=${DISPLAY}"

  if [ -z "${XDG_RUNTIME_DIR}" ]; then
    export XDG_RUNTIME_DIR=/run/user/${USER_UID}
  fi
  log.debug "XDG_RUNTIME_DIR=${XDG_RUNTIME_DIR}"

  if [ -z "${DBUS_SESSION_BUS_ADDRESS}" ]; then
    export DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/${USER_UID}/bus
  fi
  log.debug "DBUS_SESSION_BUS_ADDRESS=${DBUS_SESSION_BUS_ADDRESS}"  

  setLinuxEnvXauhority
}

setLinuxEnvXauhority() {
  if ! ${SET_LIN_ENV_XAUTHORITY}; then
    return;
  fi
  local USER_UID=`id -u`
  if [ -z "${XAUTHORITY}" ]; then
    local XAUTHORITY_VAL=`ls -1 /run/user/${USER_UID}/.mutter-Xwaylandauth* 2>/dev/null`
    if [ -n "${XAUTHORITY_VAL}" ]; then
      export XAUTHORITY=${XAUTHORITY_VAL}
    fi
  fi
  log.debug "XAUTHORITY=${XAUTHORITY}"  
}

loadConfigFiles() {
  loadKamehouseCfg
  loadScriptConfigFile
  if ${LOAD_KAMEHOUSE_SECRETS}; then
    loadKamehouseSecrets
  fi
  loadDockerContainerEnv
  customKamehouseLoadConfigFiles
}

# Override in other scripts to add custom pre parse cmd args logic
customKamehouseLoadConfigFiles() {
  return
}

# Update kamehouse config
updateKameHouseConfig() {
  local KAMEHOUSE_CONFIG_KEY=$1
  local KAMEHOUSE_CONFIG_VALUE=$2

  log.debug "Checking for valid kamehouse config key ${KAMEHOUSE_CONFIG_KEY}"
  cat ${KAMEHOUSE_CFG} | grep "${KAMEHOUSE_CONFIG_KEY}=" > /dev/null
  if [ "$?" != "0" ]; then 
    log.error "${KAMEHOUSE_CONFIG_KEY} not found in kamehouse.cfg"
    exitProcess ${EXIT_INVALID_ARG}
  fi

  sed -i -E "s/^#${KAMEHOUSE_CONFIG_KEY}=.*/${KAMEHOUSE_CONFIG_KEY}=/I" ${KAMEHOUSE_CFG}
  sed -i -E "s#^${KAMEHOUSE_CONFIG_KEY}=.*#${KAMEHOUSE_CONFIG_KEY}=${KAMEHOUSE_CONFIG_VALUE}#I" ${KAMEHOUSE_CFG}
  log.info "Set '${KAMEHOUSE_CONFIG_KEY}=${KAMEHOUSE_CONFIG_VALUE}' in kamehouse.cfg"
}
