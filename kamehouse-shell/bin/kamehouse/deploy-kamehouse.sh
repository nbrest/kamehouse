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
KAMEHOUSE_ANDROID_APP="${PROJECT_DIR}/kamehouse-mobile/platforms/android/app/build/outputs/apk/debug/app-debug.apk"
KAMEHOUSE_MOBILE_APP_SERVER="pi"
KAMEHOUSE_MOBILE_APP_USER="pi"
KAMEHOUSE_MOBILE_APP_PATH="/var/www/kamehouse-webserver/kame-house-mobile"

# Variables set by command line arguments
EXTENDED_DEPLOYMENT=false
DEPLOY_ALL_EXTRA_MODULES=false
USE_CURRENT_DIR=false

# Global variables set during the process
DEPLOYMENT_DIR=""
TOMCAT_DIR=""
TOMCAT_LOG=""
MAVEN_COMMAND=""

mainProcess() {
  setGlobalVariables
    
  if [ "${KAMEHOUSE_SERVER}" == "local" ]; then

    if ${USE_CURRENT_DIR}; then
      PROJECT_DIR=`pwd`
    else  
      cd ${PROJECT_DIR}
      checkCommandStatus "$?" "Invalid project directory" 
      pullLatestVersionFromGit
    fi
    log.info "Deploying from directory ${COL_PURPLE}${PROJECT_DIR}"
    checkCurrentDir
    deployKameHouseShell
    buildProject
    cleanLogsInGitRepoFolder
    executeOperationInTomcatManager "stop" ${TOMCAT_PORT} ${MODULE_SHORT}
    executeOperationInTomcatManager "undeploy" ${TOMCAT_PORT} ${MODULE_SHORT}
    deployToTomcat
    deployKameHouseCmd
    deployKameHouseGroot
    deployKameHouseMobile
  else
    # Execute remote deployment
    executeSshCommand       
  fi
}

checkCurrentDir() {
  if [ ! -d "./kamehouse-shell/bin" ] || [ ! -d "./.git" ]; then
    log.error "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exitProcess 1
  fi
}

setGlobalVariables() {
  loadDockerContainerEnv
  
  TOMCAT_DIR="${HOME}/programs/apache-tomcat"
  DEPLOYMENT_DIR="${TOMCAT_DIR}/webapps"
  if ${IS_LINUX_HOST}; then
    TOMCAT_LOG="${TOMCAT_DIR}/logs/catalina.out"
  else
    local LOG_DATE=`date +%Y-%m-%d`
    TOMCAT_LOG="${TOMCAT_DIR}/logs/catalina.${LOG_DATE}.log"
  fi

  SSH_SERVER=${KAMEHOUSE_SERVER}
  SSH_COMMAND="${SCRIPT_NAME} -e local -p ${MAVEN_PROFILE}"
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

buildProject() {
  log.info "Building ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} with profile ${COL_PURPLE}${MAVEN_PROFILE}${COL_DEFAULT_LOG}"
  
  exportGitCommitHash
  
  MAVEN_COMMAND="mvn clean install -P ${MAVEN_PROFILE}"
  
  if ${EXTENDED_DEPLOYMENT}; then
    log.info "Executing extended deployment. Performing checkstyle, findbugs and unit tests"
  else
    MAVEN_COMMAND="${MAVEN_COMMAND} -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true"
  fi

  if [ -n "${MODULE}" ]; then
    log.info "Building module ${COL_PURPLE}${MODULE}"
    MAVEN_COMMAND="${MAVEN_COMMAND} -pl :${MODULE} -am"
  else
    log.info "Building all modules"
  fi
  
  log.info "${MAVEN_COMMAND}"
  ${MAVEN_COMMAND}
  checkCommandStatus "$?" "An error occurred building the project ${PROJECT_DIR}"

  if [[ "${DEPLOY_ALL_EXTRA_MODULES}" == "true" || "${MODULE}" == "kamehouse-mobile" ]]; then
    log.info "Building kamehouse-mobile android app"
    cd kamehouse-mobile
    log.debug "cordova clean ; cordova platform remove android ; cordova platform add android"
    cordova clean
    cordova platform remove android
    cordova platform add android
    # Reset unnecessary git changes after platform remove/add
    git checkout HEAD -- package.json
    git checkout HEAD -- package-lock.json
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-kh-files.sh -p prod
    cp -v -f pom.xml www/
    echo "${GIT_COMMIT_HASH}" > www/git-commit-hash.txt
    date +%Y-%m-%d' '%H:%M:%S > www/build-date.txt
    log.debug "cordova build android"
    cordova build android
    checkCommandStatus "$?" "An error occurred building kamehouse-mobile"
    cd ..
  fi
}

deployToTomcat() {
  log.info "Deploying ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} to ${COL_PURPLE}${DEPLOYMENT_DIR}${COL_DEFAULT_LOG}" 
  cd ${PROJECT_DIR}

  local KAMEHOUSE_MODULES=`ls -1 | grep kamehouse-${MODULE_SHORT}`
  echo -e "${KAMEHOUSE_MODULES}" | while read KAMEHOUSE_MODULE; do
    local KAMEHOUSE_MODULE_WAR=`ls -1 ${KAMEHOUSE_MODULE}/target/*.war 2>/dev/null`
    if [ -n "${KAMEHOUSE_MODULE_WAR}" ]; then
      log.info "Deploying ${KAMEHOUSE_MODULE} in ${DEPLOYMENT_DIR}"
      cp -v ${KAMEHOUSE_MODULE_WAR} ${DEPLOYMENT_DIR}
      checkCommandStatus "$?" "An error occurred copying ${KAMEHOUSE_MODULE_WAR} to the deployment directory ${DEPLOYMENT_DIR}"
    fi
  done

  log.info "Finished deploying ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} to ${COL_PURPLE}${DEPLOYMENT_DIR}${COL_DEFAULT_LOG}"
  log.info "Execute ${COL_PURPLE}-  tail-log.sh -s ${KAMEHOUSE_SERVER} -f tomcat  -${COL_DEFAULT_LOG} to check tomcat startup progress"
}

deployKameHouseCmd() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "cmd" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-cmd${COL_DEFAULT_LOG} to ${COL_PURPLE}${KAMEHOUSE_CMD_DEPLOY_PATH}${COL_DEFAULT_LOG}" 
    cd ${PROJECT_DIR}
    mkdir -p ${KAMEHOUSE_CMD_DEPLOY_PATH}
    rm -r -f ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd
    unzip -o -q kamehouse-cmd/target/kamehouse-cmd-bundle.zip -d ${KAMEHOUSE_CMD_DEPLOY_PATH}/ 
    mv ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bt ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bat
    echo "${GIT_COMMIT_HASH}" > ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/git-commit-hash.txt
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd*
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/kamehouse-cmd*.jar
  fi
}

deployKameHouseGroot() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "groot" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-groot${COL_DEFAULT_LOG}" 
    log.info "Already pulled latest changes from git at start of deployment, so groot is up to date"
  fi
}

deployKameHouseShell() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "shell" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-shell${COL_DEFAULT_LOG}"
    cd ${PROJECT_DIR}
    chmod a+x kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
    ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
  
    if [ "${MODULE_SHORT}" == "shell" ]; then
      logFinish
      exitSuccessfully
    fi
  fi
}

deployKameHouseMobile() {
  if [[ "${DEPLOY_ALL_EXTRA_MODULES}" == "true" || "${MODULE}" == "kamehouse-mobile" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-mobile${COL_DEFAULT_LOG} app to kame.com server"
    if [ -f "${KAMEHOUSE_ANDROID_APP}" ]; then
      log.debug "scp -v ${KAMEHOUSE_ANDROID_APP} ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER}:${KAMEHOUSE_MOBILE_APP_PATH}/kamehouse.apk"
      scp -v ${KAMEHOUSE_ANDROID_APP} ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER}:${KAMEHOUSE_MOBILE_APP_PATH}/kamehouse.apk

      log.debug "ssh ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER} -C \"\\\${HOME}/programs/kamehouse-shell/bin/kamehouse/kh-mobile-regenerate-apk-html.sh -c ${GIT_COMMIT_HASH}\""
      ssh ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER} -C "\${HOME}/programs/kamehouse-shell/bin/kamehouse/kh-mobile-regenerate-apk-html.sh -c ${GIT_COMMIT_HASH}"
    else
      log.error "${KAMEHOUSE_ANDROID_APP} not found. Was the build successful?"
    fi
  fi
}

parseArguments() {
  parseKameHouseModule "$@"
  parseMavenProfile "$@"
  parseKameHouseServer "$@"

  while getopts ":acm:p:s:x" OPT; do
    case $OPT in
    ("a")
      DEPLOY_ALL_EXTRA_MODULES=true
      ;;
    ("c")
      USE_CURRENT_DIR=true
      ;;
    ("x")
      EXTENDED_DEPLOYMENT=true
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
  addHelpOption "-a" "deploy all modules, including mobile app (by default it doesn't deploy the mobile app)"
  addHelpOption "-c" "deploy from current directory instead of default ${PROJECT_DIR}"
  printKameHouseModuleOption "deploy"
  printMavenProfileOption
  printKameHouseServerOption
  addHelpOption "-x" "extended deployment. Perform checkstyle, findbugs and unit tests"
}

main "$@"
