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

IDE_LIST="(eclipse|intellij)"
DEFAULT_IDE="intellij"
IDE="${DEFAULT_IDE}"

MODULES_LIST="(admin|cmd|groot|media|mobile|shell|tennisworld|testmodule|ui|vlcrc)"
MODULE_SHORT=""
MODULE=""

MAVEN_PROFILES_LIST="(prod|qa|dev|docker|ci)"
DEFAULT_MAVEN_PROFILE="prod"
MAVEN_PROFILE="${DEFAULT_MAVEN_PROFILE}"

KAMEHOUSE_SERVERS_LIST="(docker|local|niko-nba|niko-server|niko-server-vm-ubuntu|niko-w|niko-w-vm-ubuntu|pi)"
DEFAULT_KAMEHOUSE_SERVER="local"
KAMEHOUSE_SERVER="${DEFAULT_KAMEHOUSE_SERVER}"

DEFAULT_TOMCAT_PORT=9090
TOMCAT_PORT=${DEFAULT_TOMCAT_PORT}
TOMCAT_DEBUG_PORT=8000
DEFAULT_TOMCAT_DEV_PORT=9980

DEFAULT_HTTPD_PORT=443
HTTPD_PORT=${DEFAULT_HTTPD_PORT}

KAMEHOUSE_BUILD_VERSION=
KAMEHOUSE_CMD_DEPLOY_PATH="${HOME}/programs"

# docker defaults
IS_DOCKER_CONTAINER=false
IS_REMOTE_LINUX_HOST=false
DEPLOY_TO_DOCKER=false

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
REFRESH_CORDOVA_PLUGINS=false
CLEAN_CORDOVA_BEFORE_BUILD=false
RESET_PACKAGE_JSON=false
KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN="/d/Downloads/Google Drive/KameHouse/kamehouse-mobile"
KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN="${HOME}/GoogleDrive/KameHouse/kamehouse-mobile"
KAMEHOUSE_ANDROID_APK="/kamehouse-mobile/platforms/android/app/build/outputs/apk/debug/app-debug.apk"
KAMEHOUSE_ANDROID_APK_PATH=""
CORDOVA_ANDROID_PLATFORM_VERSION="10.1.2"

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

parseIde() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -i)
        IDE="${ARGS[i+1]}"
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
      -s)
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

setEnvForIde() {
  IDE=`echo "${IDE}" | tr '[:upper:]' '[:lower:]'`
  
  if [ "${IDE}" != "eclipse" ] \
      && [ "${IDE}" != "intellij" ]; then
    log.error "Option -i ide needs to be in ${IDE_LIST}"
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
    log.error "Option -s server has an invalid value of ${KAMEHOUSE_SERVER}"
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

printIdeOption() {
  local DESCRIPTION=$1
  addHelpOption "-i ${IDE_LIST}" "${DESCRIPTION}"
}

printKameHouseModuleOption() {
  local OPERATION=$1
  addHelpOption "-m ${MODULES_LIST}" "module to ${OPERATION}"
}

printKameHouseServerOption() {
  addHelpOption "-s ${KAMEHOUSE_SERVERS_LIST}" "server to execute script on. Default is ${DEFAULT_KAMEHOUSE_SERVER}"
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

  if ${DEPLOY_TO_DOCKER}; then
    TOMCAT_PORT=${DOCKER_PORT_TOMCAT}
  fi

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
  if ! ${USE_CURRENT_DIR}; then
    log.info "Pulling latest kamehouse changes from git"
    git pull origin dev
    checkCommandStatus "$?" "Error pulling kamehouse dev branch"
  else
    log.trace "Using USE_CURRENT_DIR so skipping git pull kamehouse"
  fi
}

buildKameHouseProject() {
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-java-home.sh true true
  log.info "Building ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} with profile ${COL_PURPLE}${MAVEN_PROFILE}${COL_DEFAULT_LOG}"
  
  exportGitCommitHash
  exportBuildVersion
  exportBuildDate
  buildMavenCommand
  executeMavenCommand
  
  if [[ "${MODULE}" == "kamehouse-mobile" ]]; then
    buildMobile
  fi
  cleanLogsInGitRepoFolder
}

buildKameHouseUiStatic() {
  if [[ -z "${MODULE}" || "${MODULE}" == "kamehouse-ui" ]]; then
    cdToKameHouseModule "kamehouse-ui"
    cleanUiWebappDirectory
    log.info "Building ui static code"
    npx tsc
    cdToRootDirFromModule "kamehouse-ui"
  fi
}

cleanUiWebappDirectory() {
  log.info "Cleaning up webapp directory js files"
  rm -rf ./src/main/webapp/js
  rm -rf ./src/main/webapp/error/js
  rm -rf ./src/main/webapp/kamehouse/js
}

buildKameHouseGroot() {
  if [[ -z "${MODULE}" || "${MODULE}" == "kamehouse-groot" ]]; then
    cdToKameHouseModule "kamehouse-groot"
    cleanGrootWebappDirectory
    log.info "Building groot static code"
    npx tsc
    cdToRootDirFromModule "kamehouse-groot"
  fi
}

cleanGrootWebappDirectory() {
  log.info "Cleaning up webapp directory js files"
  rm -rf ./src/main/webapp/kame-house-groot/js
  rm -rf ./src/main/webapp/kame-house-groot/kamehouse-groot/js
}

buildKameHouseMobileStatic() {
  if [[ -z "${MODULE}" || "${MODULE}" == "kamehouse-mobile" ]]; then
    cdToKameHouseModule "kamehouse-mobile"
    cleanMobileWwwStaticDirectory
    log.info "Building mobile static code"
    npx tsc
    cdToRootDirFromModule "kamehouse-mobile"
  fi
}

cleanMobileWwwStaticDirectory() {
  log.info "Cleaning up www directory js files"
  rm -rf ./www/kame-house-mobile/js
  rm -rf ./www/kame-house-mobile/kamehouse-mobile/js
  rm -rf ./www/kame-house-mobile/kamehouse-mobile/plugin/js
}

exportGitCommitHash() {
  cdToRootDirFromModule "kamehouse-mobile"
  log.info "Exporting git commit hash to commons-core"
  GIT_COMMIT_HASH=`git rev-parse --short HEAD`
  echo "${GIT_COMMIT_HASH}" > kamehouse-commons-core/src/main/resources/git-commit-hash.txt
}

exportBuildVersion() {
  cdToRootDirFromModule "kamehouse-mobile"
  log.info "Exporting build version to commons-core"
  local KAMEHOUSE_RELEASE_VERSION=`grep -e "<version>.*1-KAMEHOUSE-SNAPSHOT</version>" pom.xml | awk '{print $1}'`
  KAMEHOUSE_RELEASE_VERSION=`echo ${KAMEHOUSE_RELEASE_VERSION:9:6}`
  echo "${KAMEHOUSE_RELEASE_VERSION}" > kamehouse-commons-core/src/main/resources/build-version.txt
}

exportBuildDate() {
  cdToRootDirFromModule "kamehouse-mobile"
  log.info "Exporting build date to commons-core"
  date +%Y-%m-%d' '%H:%M:%S > kamehouse-commons-core/src/main/resources/build-date.txt
}

buildMavenCommand() {
  MAVEN_COMMAND="mvn clean install -Dstyle.color=always -P ${MAVEN_PROFILE}"

  if ${FAST_BUILD}; then
    log.info "Executing fast build. Skipping checkstyle, findbugs and tests"
    MAVEN_COMMAND="${MAVEN_COMMAND} -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true"
  fi

  if ${INTEGRATION_TESTS}; then
    if ${CONTINUE_INTEGRATION_TESTS_ON_ERRORS}; then
      MAVEN_COMMAND="mvn clean test-compile failsafe:integration-test -Dstyle.color=always -P ${MAVEN_PROFILE}"
    else
      MAVEN_COMMAND="mvn clean test-compile failsafe:integration-test failsafe:verify -Dstyle.color=always -P ${MAVEN_PROFILE}"
    fi
  fi

  if [ -n "${MODULE}" ]; then
    log.info "Building module ${COL_PURPLE}${MODULE}"
    if ${RESUME_BUILD}; then
      log.info "Resuming from last build"
      MAVEN_COMMAND="${MAVEN_COMMAND} -rf :${MODULE}"
    else
      MAVEN_COMMAND="${MAVEN_COMMAND} -pl :${MODULE} -am"
    fi
  else
    log.info "Building all modules"
  fi
  MAVEN_COMMAND="${MAVEN_COMMAND} -Dfilter.jdbc.password=${MARIADB_PASS_KAMEHOUSE}"
}

executeMavenCommand() {
  local MAVEN_COMMAND_MASKED="${MAVEN_COMMAND}"
  MAVEN_COMMAND_MASKED="`sed 's#filter.jdbc.password=.*#filter.jdbc.password=****#' <<<"${MAVEN_COMMAND_MASKED}"`"
  log.info "${MAVEN_COMMAND_MASKED}"
  ${MAVEN_COMMAND}
  checkCommandStatus "$?" "An error occurred building the project ${PROJECT_DIR}"
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

buildMobile() {
  log.info "${COL_PURPLE}Building kamehouse-mobile app"
  setKameHouseMobileApkPath
  buildKameHouseMobileStatic
  syncStaticFilesOnMobile
  cdToKameHouseModule "kamehouse-mobile"
  setLinuxBuildEnv
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-java-home-for-mobile.sh
  if ${CLEAN_CORDOVA_BEFORE_BUILD}; then
    cleanCordovaProject
  fi
  refreshCordovaPlugins
  if ${RESET_PACKAGE_JSON}; then
    # Reset unnecessary git changes after platform remove/add
    git checkout HEAD -- package.json
    git checkout HEAD -- package-lock.json
  fi
  setCordovaPlatforms
  setMobileBuildVersionAndKeys
  updateConfigWithGitHash
  buildCordovaProject
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-java-home.sh true true
  resetConfigFromGitHash
  cdToRootDirFromModule "kamehouse-mobile"
  deleteStaticFilesOnMobile
  uploadKameHouseMobileApkToGDrive
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

setLinuxBuildEnv() {
  if ${IS_LINUX_HOST}; then
    log.info "Setting android build env"
    export ANDROID_SDK_ROOT=${HOME}/Android/Sdk
    export PATH=${PATH}:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin
  fi
}

# runs from kamehouse-mobile directory
setKameHouseMobileApkPath() {
  KAMEHOUSE_ANDROID_APK_PATH=${PROJECT_DIR}${KAMEHOUSE_ANDROID_APK}
  log.debug "Setting KAMEHOUSE_ANDROID_APK_PATH=${KAMEHOUSE_ANDROID_APK_PATH}"
}

cleanCordovaProject() {
  log.debug "cordova clean ; cordova platform remove android ; cordova platform add android@${CORDOVA_ANDROID_PLATFORM_VERSION}"
  cordova clean
  cordova platform remove android
  cordova platform add android@${CORDOVA_ANDROID_PLATFORM_VERSION}
}

# Do this only when I really want to upgrade plugins, it might break my code with newer versions
refreshCordovaPlugins() {
  if ${REFRESH_CORDOVA_PLUGINS}; then
    log.info "Refreshing cordova plugins for this build"
    # remove all plugins I ever added here, even the ones are no longer in the project
    cordova plugin remove cordova-plugin-inappbrowser # removed from project
    cordova plugin remove cordova-plugin-advanced-http
    cordova plugin remove cordova-plugin-file

    cordova plugin add cordova-plugin-advanced-http
    cordova plugin add cordova-plugin-file
  else
    log.debug "Not refreshing cordova plugins for this build"
  fi 
}

setCordovaPlatforms() {
  log.info "Setting cordova platforms"
  cordova platform add android@${CORDOVA_ANDROID_PLATFORM_VERSION}
}

# runs from root directory of kamehouse project
syncStaticFilesOnMobile() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-static-files.sh -c
}

setMobileBuildVersionAndKeys() {
  log.debug "Setting build version and encryption key"
  cp -f pom.xml www/kame-house-mobile/
  echo "${GIT_COMMIT_HASH}" > www/kame-house-mobile/git-commit-hash.txt
  date +%Y-%m-%d' '%H:%M:%S > www/kame-house-mobile/build-date.txt
  echo "${KAMEHOUSE_MOBILE_ENCRYPTION_KEY}" > www/kame-house-mobile/encryption.key
}

buildCordovaProject() {
  log.info "Executing: cordova build android"
  cordova build android
  checkCommandStatus "$?" "An error occurred building kamehouse-mobile"
}

deleteStaticFilesOnMobile() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-static-files.sh -c -d
}

updateConfigWithGitHash() {
  log.debug "Setting git commit hash on config.xml"
  cp -f config.xml config-pre-build.xml
  local RELEASE_VERSION=`grep -e "<version>.*1-KAMEHOUSE-SNAPSHOT</version>" pom.xml | awk '{print $1}'`
  RELEASE_VERSION=`echo ${RELEASE_VERSION:9:4}`
  local APP_VERSION="<widget id=\"com.nicobrest.kamehouse\" version=\"${RELEASE_VERSION}.1"
  local APP_VERSION_WITH_HASH="<widget id=\"com.nicobrest.kamehouse\" version=\"${RELEASE_VERSION}.1-${GIT_COMMIT_HASH}"

  log.debug "Setting mobile app version to: ${APP_VERSION_WITH_HASH}"
  sed -i "s+${APP_VERSION}+${APP_VERSION_WITH_HASH}+g" config.xml
}

resetConfigFromGitHash() {
  log.debug "Resetting config.xml git commit hash after build"
  mv -f config-pre-build.xml config.xml
}

uploadKameHouseMobileApkToGDrive() {
  if [ -d "${KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN}" ]; then
    log.info "${COL_PURPLE}Uploading${COL_DEFAULT_LOG} kamehouse-mobile apk ${COL_PURPLE}to google drive${COL_DEFAULT_LOG} folder ${KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN}"
    cp ${KAMEHOUSE_ANDROID_APK_PATH} "${KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN}/kamehouse.apk"
  fi

  if [ -d "${HOME}/GoogleDrive" ]; then
    log.info "Mounting google drive"
    google-drive-ocamlfuse ${HOME}/GoogleDrive
    sleep 8
  fi

  if [ -d "${KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN}" ]; then
    log.info "${COL_PURPLE}Uploading${COL_DEFAULT_LOG} kamehouse-mobile apk ${COL_PURPLE}to google drive${COL_DEFAULT_LOG} folder ${KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN}"
    cp ${KAMEHOUSE_ANDROID_APK_PATH} "${KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN}/kamehouse.apk"
  fi
}

deployToTomcat() {
  log.info "Deploying ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} to ${COL_PURPLE}${DEPLOYMENT_DIR}${COL_DEFAULT_LOG}" 

  local KAMEHOUSE_MODULES=`ls -1 | grep kamehouse-${MODULE_SHORT}`
  echo -e "${KAMEHOUSE_MODULES}" | while read KAMEHOUSE_MODULE; do
    local KAMEHOUSE_MODULE_WAR=`ls -1 ${KAMEHOUSE_MODULE}/target/*.war 2>/dev/null`
    if [ -n "${KAMEHOUSE_MODULE_WAR}" ]; then
      log.info "Deploying ${KAMEHOUSE_MODULE} in ${COL_PURPLE}${DEPLOYMENT_DIR}"
      if ${DEPLOY_TO_DOCKER}; then
        log.debug "scp -C -P ${DOCKER_PORT_SSH} ${KAMEHOUSE_MODULE_WAR} localhost:/home/${DOCKER_USERNAME}/programs/apache-tomcat/webapps"
        scp -C -P ${DOCKER_PORT_SSH} ${KAMEHOUSE_MODULE_WAR} ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/programs/apache-tomcat/webapps
      else
        cp -v ${KAMEHOUSE_MODULE_WAR} ${DEPLOYMENT_DIR}
        checkCommandStatus "$?" "An error occurred copying ${KAMEHOUSE_MODULE_WAR} to the deployment directory ${DEPLOYMENT_DIR}"
      fi
    fi
  done

  log.info "Finished deploying ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} to ${COL_PURPLE}${DEPLOYMENT_DIR}${COL_DEFAULT_LOG}"
  log.info "Execute ${COL_CYAN}\`  tail-log.sh -s ${KAMEHOUSE_SERVER} -f tomcat -n 2000 \`${COL_DEFAULT_LOG} to check tomcat startup progress"
}

deployKameHouseShell() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "shell" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-shell${COL_DEFAULT_LOG}"
    chmod a+x kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
    ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh -l ${LOG_LEVEL}
    checkCommandStatus "$?" "An error occurred deploying kamehouse-shell"

    log.info "Finished deploying ${COL_PURPLE}kamehouse-shell${COL_DEFAULT_LOG}"

    if [ "${MODULE_SHORT}" == "shell" ]; then
      exitSuccessfully
    fi
  fi
}

deployKameHouseCmd() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "cmd" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-cmd${COL_DEFAULT_LOG} to ${COL_PURPLE}${KAMEHOUSE_CMD_DEPLOY_PATH}${COL_DEFAULT_LOG}" 
    mkdir -p ${KAMEHOUSE_CMD_DEPLOY_PATH}
    rm -r -f ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd
    unzip -o -q kamehouse-cmd/target/kamehouse-cmd-bundle.zip -d ${KAMEHOUSE_CMD_DEPLOY_PATH}/ 
    mv ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bt ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bat
    local CMD_VERSION_FILE="${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/cmd-version.txt"
    echo "buildVersion=${KAMEHOUSE_BUILD_VERSION}" > ${CMD_VERSION_FILE}
    local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
    echo "buildDate=${BUILD_DATE}" >> ${CMD_VERSION_FILE}
    chmod -R 700 ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd*
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/kamehouse-cmd*.jar
    checkCommandStatus "$?" "An error occurred deploying kamehouse-cmd"
  fi
}

setKameHouseBuildVersion() {
  KAMEHOUSE_BUILD_VERSION=`getKameHouseBuildVersion`
  log.trace "KAMEHOUSE_BUILD_VERSION=${KAMEHOUSE_BUILD_VERSION}"
}