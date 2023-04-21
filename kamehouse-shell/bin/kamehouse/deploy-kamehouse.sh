#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/.kamehouse/.shell/.cred

# Initial config
DEFAULT_ENV=local
LOG_PROCESS_TO_FILE=true
PROJECT_DIR="${HOME}/git/${PROJECT}"
KAMEHOUSE_CMD_DEPLOY_PATH="${HOME}/programs"
KAMEHOUSE_MOBILE_APP_SERVER="pi"
KAMEHOUSE_MOBILE_APP_USER="pi"
KAMEHOUSE_MOBILE_APP_PATH="/var/www/kamehouse-webserver/kame-house-mobile"

# Global variables set during the process
DEPLOYMENT_DIR=""
TOMCAT_DIR=""
TOMCAT_LOG=""
KAMEHOUSE_BUILD_VERSION=""
DEPLOY_TO_TOMCAT=false

USE_CURRENT_DIR_FOR_DEPLOYMENT=false

# buildMavenCommand default settings override for deployment
FAST_BUILD=true

# buildMobile default settings override for deployment
RESET_PACKAGE_JSON=true

mainProcess() {
  setDeploymentParameters
  if [ "${KAMEHOUSE_SERVER}" == "local" ]; then
    doLocalDeployment
  else
    # Execute remote deployment
    setSshParameters
    executeSshCommand
  fi
}

doLocalDeployment() {
  if ${USE_CURRENT_DIR_FOR_DEPLOYMENT}; then
    PROJECT_DIR=`pwd`
  else  
    cd ${PROJECT_DIR}
    checkCommandStatus "$?" "Invalid project directory" 
    pullLatestVersionFromGit
  fi
  log.info "Deploying from directory ${COL_PURPLE}${PROJECT_DIR}"
  checkCurrentDir
  setKameHouseBuildVersion
  deployKameHouseShell
  deployKameHouseUiStatic
  deployKameHouseGroot
  buildKameHouseProject
  if ${DEPLOY_TO_TOMCAT}; then
    executeOperationInTomcatManager "stop" ${TOMCAT_PORT} ${MODULE_SHORT}
    executeOperationInTomcatManager "undeploy" ${TOMCAT_PORT} ${MODULE_SHORT}
    deployToTomcat
  fi
  deployKameHouseCmd
  deployKameHouseMobile
  cleanUpM2
}

checkCurrentDir() {
  if [ ! -d "./kamehouse-shell/bin" ] || [ ! -d "./.git" ]; then
    log.error "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exitProcess 1
  fi
}

setKameHouseBuildVersion() {
  KAMEHOUSE_BUILD_VERSION=`getKameHouseBuildVersion`
  log.trace "KAMEHOUSE_BUILD_VERSION=${KAMEHOUSE_BUILD_VERSION}"
}

setDeploymentParameters() {
  loadDockerContainerEnv
  
  TOMCAT_DIR="${HOME}/programs/apache-tomcat"
  DEPLOYMENT_DIR="${TOMCAT_DIR}/webapps"
  if ${IS_LINUX_HOST}; then
    TOMCAT_LOG="${TOMCAT_DIR}/logs/catalina.out"
  else
    local LOG_DATE=`date +%Y-%m-%d`
    TOMCAT_LOG="${TOMCAT_DIR}/logs/catalina.${LOG_DATE}.log"
  fi

  if [ -n "${MODULE_SHORT}" ]; then
    if [ "${MODULE_SHORT}" == "admin" ] ||
       [ "${MODULE_SHORT}" == "media" ] ||
       [ "${MODULE_SHORT}" == "tennisworld" ] ||
       [ "${MODULE_SHORT}" == "testmodule" ] ||
       [ "${MODULE_SHORT}" == "ui" ] ||
       [ "${MODULE_SHORT}" == "vlcrc" ]; then
      DEPLOY_TO_TOMCAT=true
    fi
  else
    DEPLOY_TO_TOMCAT=true
  fi
}

setSshParameters() {
  SSH_SERVER=${KAMEHOUSE_SERVER}
  SSH_COMMAND="${SCRIPT_NAME} -s local -p ${MAVEN_PROFILE}"
  if [ -n "${MODULE_SHORT}" ]; then
    SSH_COMMAND=${SSH_COMMAND}" -m "${MODULE_SHORT}
  fi 

  if [ "${KAMEHOUSE_SERVER}" == "aws" ]; then
    SSH_SERVER=${AWS_SSH_SERVER}
    SSH_USER=${AWS_SSH_USER}
  fi
}

pullLatestVersionFromGit() {
  if [ "${IS_DOCKER_CONTAINER}" == "true" ] && [ "${DOCKER_PROFILE}" == "dev" ]; then
    log.info "Running on a dev docker container. Skipping reset git branch"
  else
    log.info "Pulling latest version of dev branch of ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} from repository"     
    git checkout dev
    checkCommandStatus "$?" "An error occurred checking out dev branch"
    
    git reset --hard

    git pull origin dev
    checkCommandStatus "$?" "An error occurred pulling origin dev"
  fi
}

deployToTomcat() {
  log.info "Deploying ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} to ${COL_PURPLE}${DEPLOYMENT_DIR}${COL_DEFAULT_LOG}" 
  cd ${PROJECT_DIR}

  local KAMEHOUSE_MODULES=`ls -1 | grep kamehouse-${MODULE_SHORT}`
  echo -e "${KAMEHOUSE_MODULES}" | while read KAMEHOUSE_MODULE; do
    local KAMEHOUSE_MODULE_WAR=`ls -1 ${KAMEHOUSE_MODULE}/target/*.war 2>/dev/null`
    if [ -n "${KAMEHOUSE_MODULE_WAR}" ]; then
      log.info "Deploying ${COL_PURPLE}${KAMEHOUSE_MODULE}${COL_DEFAULT_LOG} in ${COL_PURPLE}${DEPLOYMENT_DIR}"
      cp -v ${KAMEHOUSE_MODULE_WAR} ${DEPLOYMENT_DIR}
      checkCommandStatus "$?" "An error occurred copying ${KAMEHOUSE_MODULE_WAR} to the deployment directory ${DEPLOYMENT_DIR}"
    fi
  done

  log.info "Finished deploying ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} to ${COL_PURPLE}${DEPLOYMENT_DIR}${COL_DEFAULT_LOG}"
  log.info "Execute ${COL_CYAN}\`  tail-log.sh -s ${KAMEHOUSE_SERVER} -f tomcat  \`${COL_DEFAULT_LOG} to check tomcat startup progress"
}

deployKameHouseCmd() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "cmd" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-cmd${COL_DEFAULT_LOG} to ${COL_PURPLE}${KAMEHOUSE_CMD_DEPLOY_PATH}${COL_DEFAULT_LOG}" 
    cd ${PROJECT_DIR}
    mkdir -p ${KAMEHOUSE_CMD_DEPLOY_PATH}
    rm -r -f ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd
    unzip -o -q kamehouse-cmd/target/kamehouse-cmd-bundle.zip -d ${KAMEHOUSE_CMD_DEPLOY_PATH}/ 
    mv ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bt ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bat
    local CMD_VERSION_FILE="${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/cmd-version.txt"
    echo "buildVersion=${KAMEHOUSE_BUILD_VERSION}" > ${CMD_VERSION_FILE}
    local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
    echo "buildDate=${BUILD_DATE}" >> ${CMD_VERSION_FILE}
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd*
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/kamehouse-cmd*.jar
  fi
}

deployKameHouseUiStatic() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "ui" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-ui static content${COL_DEFAULT_LOG}"
    local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
    mkdir -p ${HTTPD_CONTENT_ROOT}
    rm -rf ${HTTPD_CONTENT_ROOT}/kame-house
    cp -rf ./kamehouse-ui/src/main/webapp ${HTTPD_CONTENT_ROOT}/kame-house
    rm -rf ${HTTPD_CONTENT_ROOT}/kame-house/WEB-INF
    log.info "Finished deploying ${COL_PURPLE}kamehouse-ui static content${COL_DEFAULT_LOG}"
  fi
}

deployKameHouseGroot() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "groot" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-groot${COL_DEFAULT_LOG}" 
    local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
    mkdir -p ${HTTPD_CONTENT_ROOT}
    rm -rf ${HTTPD_CONTENT_ROOT}/kame-house-groot
    cp -rf ./kamehouse-groot/public/kame-house-groot ${HTTPD_CONTENT_ROOT}/
    
    local GROOT_VERSION_FILE="${HTTPD_CONTENT_ROOT}/kame-house-groot/groot-version.txt"
    echo "buildVersion=${KAMEHOUSE_BUILD_VERSION}" > ${GROOT_VERSION_FILE}
    local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
    echo "buildDate=${BUILD_DATE}" >> ${GROOT_VERSION_FILE}

    log.info "Finished deploying ${COL_PURPLE}kamehouse-groot${COL_DEFAULT_LOG}"

    if [ "${MODULE_SHORT}" == "groot" ]; then
      logFinish
      exitSuccessfully
    fi
  fi
}

deployKameHouseShell() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "shell" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-shell${COL_DEFAULT_LOG}"
    cd ${PROJECT_DIR}
    chmod a+x kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
    ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
  
    log.info "Finished deploying ${COL_PURPLE}kamehouse-shell${COL_DEFAULT_LOG}"

    if [ "${MODULE_SHORT}" == "shell" ]; then
      logFinish
      exitSuccessfully
    fi
  fi
}

deployKameHouseMobile() {
  if [[ "${MODULE}" == "kamehouse-mobile" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-mobile${COL_DEFAULT_LOG} app to kame.com server"
    if [ -f "${KAMEHOUSE_ANDROID_APK_PATH}" ]; then
      log.debug "scp -v ${KAMEHOUSE_ANDROID_APK_PATH} ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER}:${KAMEHOUSE_MOBILE_APP_PATH}/kamehouse.apk"
      scp -v ${KAMEHOUSE_ANDROID_APK_PATH} ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER}:${KAMEHOUSE_MOBILE_APP_PATH}/kamehouse.apk

      log.debug "ssh ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER} -C \"\\\${HOME}/programs/kamehouse-shell/bin/kamehouse/kh-mobile-regenerate-apk-html.sh -c ${GIT_COMMIT_HASH}\""
      ssh ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER} -C "\${HOME}/programs/kamehouse-shell/bin/kamehouse/kh-mobile-regenerate-apk-html.sh -c ${GIT_COMMIT_HASH}"
    else
      log.error "${KAMEHOUSE_ANDROID_APK_PATH} not found. Was the build successful?"
    fi
  fi
}

cleanUpM2() {
  log.info "Removing com.nicobrest entries from ${HOME}/.m2"
  rm -rf ${HOME}/.m2/repository/com/nicobrest
}

parseArguments() {
  parseKameHouseModule "$@"
  parseMavenProfile "$@"
  parseKameHouseServer "$@"

  while getopts ":bcm:p:s:" OPT; do
    case $OPT in
    ("b")
      REFRESH_CORDOVA_PLUGINS=true
      ;;
    ("c")
      USE_CURRENT_DIR_FOR_DEPLOYMENT=true
      USE_CURRENT_DIR_FOR_CORDOVA=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  setEnvForKameHouseModule
  setEnvForMavenProfile
  setEnvForKameHouseServer
}

printHelpOptions() {
  addHelpOption "-b" "mobile: refresh cordova plugins ${COL_YELLOW}USE WHEN VERY SURE"
  addHelpOption "-c" "deploy from current directory instead of default ${PROJECT_DIR}"
  printKameHouseModuleOption "deploy"
  printMavenProfileOption
  printKameHouseServerOption
}

main "$@"
