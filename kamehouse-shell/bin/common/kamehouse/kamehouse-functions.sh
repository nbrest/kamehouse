export PATH="${HOME}/programs/apache-maven/bin:${PATH}"

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
USE_CURRENT_DIR_FOR_CORDOVA=false
REFRESH_CORDOVA_PLUGINS=false
CLEAN_CORDOVA_BEFORE_BUILD=false
RESET_PACKAGE_JSON=false
KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN="/d/Downloads/Google Drive/KameHouse/kamehouse-mobile"
KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN="${HOME}/GoogleDrive/KameHouse/kamehouse-mobile"
KAMEHOUSE_ANDROID_APK="/platforms/android/app/build/outputs/apk/debug/app-debug.apk"
KAMEHOUSE_ANDROID_APK_PATH=""

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
    exitProcess 1
  fi
}

setEnvForIde() {
  IDE=`echo "${IDE}" | tr '[:upper:]' '[:lower:]'`
  
  if [ "${IDE}" != "eclipse" ] \
      && [ "${IDE}" != "intellij" ]; then
    log.error "Option -i ide needs to be in ${IDE_LIST}"
    printHelp
    exitProcess 1
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
      exitProcess 1
    fi
  fi
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
    log.error "Option -s server has an invalid value of ${KAMEHOUSE_SERVER}"
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

setEnvForTomcatPort() {
  local REGEX_NUMBER='^[0-9]+$'
  if [[ ! ${TOMCAT_PORT} =~ $REGEX_NUMBER ]]; then
    log.error "Option -p has an invalid value of ${TOMCAT_PORT}"
    printHelp
    exitProcess 1
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

# Set a kamehouse command to execute through exec-script.sh or sudo or as root
setSudoKameHouseCommand() {
  log.warn "This script needs to run as ${COL_RED}root${COL_DEFAULT_LOG} or with ${COL_RED}sudo${COL_DEFAULT_LOG} or with ${COL_RED}exec-script.sh${COL_DEFAULT_LOG}"  
  SUDO_KAMEHOUSE_COMMAND=$1
  if ${IS_LINUX_HOST}; then
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

buildKameHouseProject() {
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-java-home.sh
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

# Assumes it's running on the root of the git kamehouse project
exportGitCommitHash() {
  local CURRENT_DIR=`basename $(pwd)`
  if [ "${CURRENT_DIR}" == "kamehouse-mobile" ]; then
    cd ..
  fi
  log.info "Exporting git commit hash to commons-core"
  GIT_COMMIT_HASH=`git rev-parse --short HEAD`
  echo "${GIT_COMMIT_HASH}" > kamehouse-commons-core/src/main/resources/git-commit-hash.txt
}

exportBuildVersion() {
  local CURRENT_DIR=`basename $(pwd)`
  if [ "${CURRENT_DIR}" == "kamehouse-mobile" ]; then
    cd ..
  fi
  log.info "Exporting build version to commons-core"
  local KAMEHOUSE_RELEASE_VERSION=`grep -e "<version>.*1-KAMEHOUSE-SNAPSHOT</version>" pom.xml | awk '{print $1}'`
  KAMEHOUSE_RELEASE_VERSION=`echo ${KAMEHOUSE_RELEASE_VERSION:9:6}`
  echo "${KAMEHOUSE_RELEASE_VERSION}" > kamehouse-commons-core/src/main/resources/build-version.txt
}

exportBuildDate() {
  local CURRENT_DIR=`basename $(pwd)`
  if [ "${CURRENT_DIR}" == "kamehouse-mobile" ]; then
    cd ..
  fi
  log.info "Exporting build date to commons-core"
  date +%Y-%m-%d' '%H:%M:%S > kamehouse-commons-core/src/main/resources/build-date.txt
}

buildMavenCommand() {
  MAVEN_COMMAND="mvn clean install -P ${MAVEN_PROFILE}"

  if ${FAST_BUILD}; then
    log.info "Executing fast build. Skipping checkstyle, findbugs and tests"
    MAVEN_COMMAND="${MAVEN_COMMAND} -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true"
  fi

  if ${INTEGRATION_TESTS}; then
    if ${CONTINUE_INTEGRATION_TESTS_ON_ERRORS}; then
      MAVEN_COMMAND="mvn test-compile failsafe:integration-test -P ${MAVEN_PROFILE}"
    else
      MAVEN_COMMAND="mvn test-compile failsafe:integration-test failsafe:verify -P ${MAVEN_PROFILE}"
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
}

executeMavenCommand() {
  log.info "${MAVEN_COMMAND}"
  ${MAVEN_COMMAND}
  checkCommandStatus "$?" "An error occurred building the project ${PROJECT_DIR}"
}

cleanLogsInGitRepoFolder() {
  local CURRENT_DIR=`basename $(pwd)`
  if [ "${CURRENT_DIR}" == "kamehouse-mobile" ]; then
    cd ..
  fi
  log.debug "Clearing logs in git repo folder"
  rm -v -f logs/*.log

  local KAMEHOUSE_MODULES=`ls -1 | grep kamehouse-`
  echo -e "${KAMEHOUSE_MODULES}" | while read KAMEHOUSE_MODULE; do
    rm -v -f ${KAMEHOUSE_MODULE}/logs/*.log
  done
}

buildMobile() {
  log.info "${COL_PURPLE}Building kamehouse-mobile app"
  cd kamehouse-mobile
  setLinuxBuildEnv
  setKameHouseMobileApkPath
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
  syncStaticUiFilesOnMobile
  setMobileBuildVersionAndKeys
  updateConfigWithGitHash
  buildCordovaProject
  resetConfigFromGitHash
  deleteStaticUiFilesOnMobile
  uploadKameHouseMobileApkToGDrive
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-java-home.sh
}

setLinuxBuildEnv() {
  if ${IS_LINUX_HOST}; then
    log.info "Setting android build env"
    export ANDROID_SDK_ROOT=${HOME}/Android/Sdk
    export PATH=${PATH}:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin
  fi
}

setKameHouseMobileApkPath() {
  local CURRENT_DIR=`basename $(pwd)`
  if [ "${CURRENT_DIR}" == "kamehouse" ]; then
    cd kamehouse-mobile
  fi
  KAMEHOUSE_ANDROID_APK_PATH=`pwd`${KAMEHOUSE_ANDROID_APK}
  log.debug "Setting KAMEHOUSE_ANDROID_APK_PATH=${KAMEHOUSE_ANDROID_APK_PATH}"
}

cleanCordovaProject() {
  log.debug "cordova clean ; cordova platform remove android ; cordova platform add android"
  cordova clean
  cordova platform remove android
  cordova platform add android
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

syncStaticUiFilesOnMobile() {
  if ${USE_CURRENT_DIR_FOR_CORDOVA}; then
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-kh-files.sh
  else
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-kh-files.sh -s prod
  fi
}

setMobileBuildVersionAndKeys() {
  log.debug "Setting build version and encryption key"
  cp -f pom.xml www/kame-house-mobile/
  echo "${GIT_COMMIT_HASH}" > www/kame-house-mobile/git-commit-hash.txt
  date +%Y-%m-%d' '%H:%M:%S > www/kame-house-mobile/build-date.txt
  echo "${KAMEHOUSE_MOBILE_ENCRYPTION_KEY}" > www/kame-house-mobile/encryption.key
}

buildCordovaProject() {
  log.debug "Executiing: cordova build android"
  cordova build android
  checkCommandStatus "$?" "An error occurred building kamehouse-mobile"
}

deleteStaticUiFilesOnMobile() {
  if ${USE_CURRENT_DIR_FOR_CORDOVA}; then
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-kh-files.sh -d
  else
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-kh-files.sh -s prod -d
  fi
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
  cd ${PROJECT_DIR}

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
  log.info "Execute ${COL_CYAN}\`  tail-log.sh -s ${KAMEHOUSE_SERVER} -f tomcat  \`${COL_DEFAULT_LOG} to check tomcat startup progress"
}