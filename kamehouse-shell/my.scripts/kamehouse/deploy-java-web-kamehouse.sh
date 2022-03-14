#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/my.scripts/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/my.scripts/.cred/.cred

# Initial config
DEFAULT_ENV=local
LOG_PROCESS_TO_FILE=true
PROJECT_DIR="${HOME}/git/${PROJECT}"
KAMEHOUSE_CMD_DEPLOY_PATH="${HOME}/programs"
KAMEHOUSE_ANDROID_APP="${PROJECT_DIR}/kamehouse-mobile/platforms/android/app/build/outputs/apk/debug/app-debug.apk"
KAMEHOUSE_MOBILE_APP_SERVER="pi"
KAMEHOUSE_MOBILE_APP_USER="pi"
KAMEHOUSE_MOBILE_APP_PATH="/var/www/kh.webserver/kame-house-mobile"

# Variables set by command line arguments
MAVEN_PROFILE="prod"
FAST_DEPLOYMENT=false
MODULE=
MODULE_SHORT=
DEPLOY_ALL_EXTRA_MODULES=false

# Global variables set during the process
COPY_COMMAND=""
DEPLOYMENT_DIR=""
TOMCAT_DIR=""
TOMCAT_LOG=""
MAVEN_COMMAND=""
GIT_COMMIT_HASH=""

mainProcess() {
  setGlobalVariables
    
  if [ "${ENVIRONMENT}" == "local" ]; then
    cd ${PROJECT_DIR}
    checkCommandStatus "$?" "Invalid project directory" 
    pullLatestVersionFromGit
    buildProject
    cleanLogsInGitRepoFolder
    executeOperationInTomcatManager "stop" ${TOMCAT_PORT} ${MODULE_SHORT}
    executeOperationInTomcatManager "undeploy" ${TOMCAT_PORT} ${MODULE_SHORT}
    deployToTomcat
    deployKameHouseCmd
    deployKameHouseGroot
    deployKameHouseShell
    deployKameHouseMobile
  else
    # Execute remote deployment
    executeSshCommand       
  fi
}

parseArguments() {
  while getopts ":ae:fhm:p:" OPT; do
    case $OPT in
    ("a")
      DEPLOY_ALL_EXTRA_MODULES=true
      ;;
    ("e")
      parseEnvironment "$OPTARG"
      ;;
    ("f")
      FAST_DEPLOYMENT=true
      ;;
    ("h")
      parseHelp
      ;;
    ("m")
      MODULE="kamehouse-$OPTARG"
      MODULE_SHORT="$OPTARG"
      ;;
    ("p")
      local PROFILE_ARG=$OPTARG 
      PROFILE_ARG=`echo "${PROFILE_ARG}" | tr '[:upper:]' '[:lower:]'`
      
      if [ "${PROFILE_ARG}" != "prod" ] \
          && [ "${PROFILE_ARG}" != "qa" ] \
          && [ "${PROFILE_ARG}" != "dev" ] \
          && [ "${PROFILE_ARG}" != "docker" ] \
          && [ "${PROFILE_ARG}" != "ci" ]; then
        log.error "Option -p profile needs to be prod, qa, dev or ci"
        printHelp
        exitProcess 1
      fi
            
      MAVEN_PROFILE=${PROFILE_ARG}
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
  
  if [ -z "${ENVIRONMENT}" ]
  then
    log.warn "Option -e environment is not set. Using default environment ${COL_PURPLE}${DEFAULT_ENV}"
    ENVIRONMENT=${DEFAULT_ENV}
  fi
}

setGlobalVariables() {
  loadDockerContainerEnv
  
  TOMCAT_DIR="${HOME}/programs/apache-tomcat"
  DEPLOYMENT_DIR="${TOMCAT_DIR}/webapps"
  if ${IS_LINUX_HOST}; then
    TOMCAT_LOG="${TOMCAT_DIR}/logs/catalina.out"
    if [ "${HOME}" == "/root" ]; then
      COPY_COMMAND="cp"
    else
      COPY_COMMAND="sudo cp"
    fi
  else
    local LOG_DATE=`date +%Y-%m-%d`
    TOMCAT_LOG="${TOMCAT_DIR}/logs/catalina.${LOG_DATE}.log"
    COPY_COMMAND="cp"
  fi

  SSH_SERVER=${ENVIRONMENT}
  SSH_COMMAND="${SCRIPT_NAME} -e local -p ${MAVEN_PROFILE}"

  if [ "${ENVIRONMENT}" == "aws" ]; then
    SSH_SERVER=${AWS_SSH_SERVER}
    SSH_USER=${AWS_SSH_USER}
  fi
}

pullLatestVersionFromGit() {
  log.info "Pulling latest version of dev branch of ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} from repository"     
  git checkout dev
  checkCommandStatus "$?" "An error occurred checking out dev branch"
  
  git reset --hard

  git pull origin dev
  checkCommandStatus "$?" "An error occurred pulling origin dev"
}

buildProject() {
  log.info "Building ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} with profile ${COL_PURPLE}${MAVEN_PROFILE}${COL_DEFAULT_LOG}"
  
  exportGitCommitHash
  
  MAVEN_COMMAND="mvn clean install -P ${MAVEN_PROFILE}"
  
  if ${FAST_DEPLOYMENT}; then
    log.info "Executing fast deployment. Skipping checkstyle, findbugs and tests"
    MAVEN_COMMAND="${MAVEN_COMMAND} -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true"
  fi

  if [ -n "${MODULE}" ]; then
    log.info "Building module ${COL_PURPLE}${MODULE}"
    MAVEN_COMMAND="${MAVEN_COMMAND} -pl :${MODULE} -am"
  else
    log.info "Building all modules"
  fi
  
  ${MAVEN_COMMAND}
  checkCommandStatus "$?" "An error occurred building the project ${PROJECT_DIR}"

  if [[ "${DEPLOY_ALL_EXTRA_MODULES}" == "true" || "${MODULE}" == "kamehouse-mobile" ]]; then
    log.info "Building kamehouse-mobile android app"
    cd kamehouse-mobile
    cordova clean
    cordova platform remove android
    cordova platform add android
    # Reset unnecessary git changes after platform remove/add
    git checkout HEAD -- package.json
    git checkout HEAD -- package-lock.json
    ${HOME}/my.scripts/kamehouse/kamehouse-mobile-resync-kh-files.sh -p prod
    cp -v -f pom.xml www/
    GIT_COMMIT_HASH=`git rev-parse --short HEAD`
    echo "${GIT_COMMIT_HASH}" > www/git-commit-hash.txt
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
      ${COPY_COMMAND} -v ${KAMEHOUSE_MODULE_WAR} ${DEPLOYMENT_DIR}
      checkCommandStatus "$?" "An error occurred copying ${KAMEHOUSE_MODULE_WAR} to the deployment directory ${DEPLOYMENT_DIR}"
    fi
  done

  log.info "Finished deploying ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} to ${COL_PURPLE}${DEPLOYMENT_DIR}${COL_DEFAULT_LOG}"
  log.info "Execute ${COL_PURPLE}-  tail-log.sh -e ${ENVIRONMENT} -f tomcat  -${COL_DEFAULT_LOG} to check tomcat startup progress"
}

deployKameHouseCmd() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "cmd" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-cmd${COL_DEFAULT_LOG} to ${COL_PURPLE}${KAMEHOUSE_CMD_DEPLOY_PATH}${COL_DEFAULT_LOG}" 
    cd ${PROJECT_DIR}
    mkdir -p ${KAMEHOUSE_CMD_DEPLOY_PATH}
    rm -r -f ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd
    unzip -o -q kamehouse-cmd/target/kamehouse-cmd-bundle.zip -d ${KAMEHOUSE_CMD_DEPLOY_PATH}/ 
    mv ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bt ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bat
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
    if ${IS_DOCKER_CONTAINER}; then
      log.info "Inside a docker container, rebuilding my.scripts directory"
      /home/nbrest/my.scripts/kamehouse/docker/docker-my-scripts-update.sh
    else
      git-pull-my-scripts.sh
    fi
  fi
}

deployKameHouseMobile() {
  if [[ "${DEPLOY_ALL_EXTRA_MODULES}" == "true" || "${MODULE}" == "kamehouse-mobile" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-mobile${COL_DEFAULT_LOG} app to kame.com server"
    if [ -f "${KAMEHOUSE_ANDROID_APP}" ]; then
      scp -v ${KAMEHOUSE_ANDROID_APP} ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER}:${KAMEHOUSE_MOBILE_APP_PATH}/kamehouse.apk
      ssh ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER} -C "cd ${KAMEHOUSE_MOBILE_APP_PATH} ; echo 'sha256sum:' > kamehouse.apk.sha256.txt ; sha256sum kamehouse.apk >> kamehouse.apk.sha256.txt ; ls -ln | cut -d ' ' -f 5- >> kamehouse.apk.sha256.txt ; echo "" >> kamehouse.apk.sha256.txt ; echo 'git commit hash: '${GIT_COMMIT_HASH} >> kamehouse.apk.sha256.txt"
    else
      log.error "${KAMEHOUSE_ANDROID_APP} not found. Was the build successful?"
    fi
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-a${COL_NORMAL} deploy all modules, including mobile app (by default it doesn't deploy the mobile app)"
  echo -e "     ${COL_BLUE}-e (aws|local|niko-nba|niko-server|niko-server-vm-ubuntu|niko-w|niko-w-vm-ubuntu)${COL_NORMAL} environment to build and deploy to. Default is local if not specified"
  echo -e "     ${COL_BLUE}-f${COL_NORMAL} fast deployment. Skip checkstyle, findbugs and tests" 
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-m (admin|cmd|groot|media|mobile|shell|tennisworld|testmodule|ui|vlcrc)${COL_NORMAL} module to deploy"
  echo -e "     ${COL_BLUE}-p (prod|qa|dev|docker|ci)${COL_NORMAL} maven profile to build the project with. Default is prod if not specified"
}

main "$@"
