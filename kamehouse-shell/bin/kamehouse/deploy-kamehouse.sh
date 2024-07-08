#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi
source ${HOME}/.kamehouse/.shell/.cred

# Initial config
DEFAULT_ENV=local
KAMEHOUSE_MOBILE_APP_SERVER="pi"
KAMEHOUSE_MOBILE_APP_USER="pi"
KAMEHOUSE_MOBILE_APP_PATH="/var/www/kamehouse-webserver/kame-house-mobile"

# Global variables set during the process
DEPLOYMENT_DIR=""
TOMCAT_DIR=""
TOMCAT_LOG=""
KAMEHOUSE_BUILD_VERSION=""
DEPLOY_TO_TOMCAT=false
LOG_LEVEL=INFO

# buildMavenCommand default settings override for deployment
FAST_BUILD=true

# buildMobile default settings override for deployment
RESET_PACKAGE_JSON=true

EXIT_CODE=${EXIT_SUCCESS}

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
  setKameHouseRootProjectDir
  if ! ${USE_CURRENT_DIR}; then
    pullLatestVersionFromGit
  fi
  setKameHouseBuildVersion
  deployKameHouseShell
  buildKameHouseUiStatic
  deployKameHouseUiStatic
  buildKameHouseGroot
  deployKameHouseGroot
  buildKameHouseProject
  if ${DEPLOY_TO_TOMCAT}; then
    executeOperationInTomcatManager "stop" ${TOMCAT_PORT} ${MODULE_SHORT}
    executeOperationInTomcatManager "undeploy" ${TOMCAT_PORT} ${MODULE_SHORT}
    deployToTomcat
  fi
  deployKameHouseCmd
  deployKameHouseMobile
  cleanUpMavenRepository
  checkForErrors 
}

checkForErrors() {
  if [ "${EXIT_CODE}" != "0" ]; then
    log.error "Error executing kamehouse deployment"
    exitProcess ${EXIT_CODE}
  fi
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
  SSH_COMMAND="${SCRIPT_NAME} -s local -p ${MAVEN_PROFILE}"
  if [ -n "${MODULE_SHORT}" ]; then
    SSH_COMMAND=${SSH_COMMAND}" -m "${MODULE_SHORT}
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

deployKameHouseUiStatic() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "ui" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-ui static content${COL_DEFAULT_LOG}"
    local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
    mkdir -p ${HTTPD_CONTENT_ROOT}
    rm -rf ${HTTPD_CONTENT_ROOT}/kame-house
    cp -rf ./kamehouse-ui/src/main/webapp ${HTTPD_CONTENT_ROOT}/kame-house
    rm -rf ${HTTPD_CONTENT_ROOT}/kame-house/WEB-INF
    checkCommandStatus "$?" "An error occurred deploying kamehouse ui static content"

    local FILES=`find ${HTTPD_CONTENT_ROOT}/kame-house -name '.*' -prune -o -type f`
    while read FILE; do
      if [ -n "${FILE}" ]; then
        chmod a+rx ${FILE}
      fi
    done <<< ${FILES}

    local DIRECTORIES=`find ${HTTPD_CONTENT_ROOT}/kame-house -name '.*' -prune -o -type d`
    while read DIRECTORY; do
      if [ -n "${DIRECTORY}" ]; then
        chmod a+rx ${DIRECTORY}
      fi
    done <<< ${DIRECTORIES}

    log.info "Finished deploying ${COL_PURPLE}kamehouse-ui static content${COL_DEFAULT_LOG}"
  fi
}

deployKameHouseGroot() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "groot" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-groot${COL_DEFAULT_LOG}" 
    local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
    mkdir -p ${HTTPD_CONTENT_ROOT}
    rm -rf ${HTTPD_CONTENT_ROOT}/kame-house-groot
    cp -rf ./kamehouse-groot/src/main/webapp/kame-house-groot ${HTTPD_CONTENT_ROOT}/
    checkCommandStatus "$?" "An error occurred deploying kamehouse groot"

    local FILES=`find ${HTTPD_CONTENT_ROOT}/kame-house-groot -name '.*' -prune -o -type f`
    while read FILE; do
      if [ -n "${FILE}" ]; then
        chmod a+rx ${FILE}
      fi
    done <<< ${FILES}

    local DIRECTORIES=`find ${HTTPD_CONTENT_ROOT}/kame-house-groot -name '.*' -prune -o -type d`
    while read DIRECTORY; do
      if [ -n "${DIRECTORY}" ]; then
        chmod a+rx ${DIRECTORY}
      fi
    done <<< ${DIRECTORIES}

    local GROOT_VERSION_FILE="${HTTPD_CONTENT_ROOT}/kame-house-groot/groot-version.txt"
    echo "buildVersion=${KAMEHOUSE_BUILD_VERSION}" > ${GROOT_VERSION_FILE}
    local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
    echo "buildDate=${BUILD_DATE}" >> ${GROOT_VERSION_FILE}

    log.info "Finished deploying ${COL_PURPLE}kamehouse-groot${COL_DEFAULT_LOG}"

    if [ "${MODULE_SHORT}" == "groot" ]; then
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
      checkCommandStatus "$?" "An error occurred deploying kamehouse-mobile through ssh"

      log.debug "ssh ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER} -C \"\\\${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-regenerate-apk-html.sh -b ${KAMEHOUSE_BUILD_VERSION}\""
      ssh ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER} -C "\${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-regenerate-apk-html.sh -b ${KAMEHOUSE_BUILD_VERSION}"
      checkCommandStatus "$?" "An error occurred regenerating apk html"

    else
      log.error "${KAMEHOUSE_ANDROID_APK_PATH} not found. Was the build successful?"
      EXIT_CODE=${EXIT_ERROR}
    fi
  fi
}

parseArguments() {
  parseKameHouseModule "$@"
  parseMavenProfile "$@"
  parseKameHouseServer "$@"

  while getopts ":bcm:l:p:s:" OPT; do
    case $OPT in
    ("b")
      REFRESH_CORDOVA_PLUGINS=true
      ;;
    ("c")
      USE_CURRENT_DIR=true
      ;;
    ("l")
      LOG_LEVEL=$OPTARG
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
  addHelpOption "-c" "deploy current version of the current directory without pulling latest version. Default deployment dir: ${PROJECT_DIR}"
  addHelpOption "-l [ERROR|WARN|INFO|DEBUG|TRACE]" "set log level for scripts. Default is INFO"
  printKameHouseModuleOption "deploy"
  printMavenProfileOption
  printKameHouseServerOption
}

main "$@"
