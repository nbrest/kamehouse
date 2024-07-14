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

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/.kamehouse/.shell/.cred

DEPLOYMENT_DIR=
TOMCAT_PORT=${DEFAULT_TOMCAT_DEV_PORT}
FAST_BUILD=true
DEPLOY_TO_TOMCAT=false
STATIC_ONLY=false

mainProcess() {
  setGlobalVariables
  setKameHouseRootProjectDir
  setKameHouseBuildVersion
  deployKameHouseShell
  buildKameHouseUiStatic
  deployKameHouseUiStatic
  buildKameHouseGroot
  deployKameHouseGroot
  buildKameHouseMobileStatic
  deployKameHouseMobileStatic
  if ${STATIC_ONLY}; then
    log.info "Finished deploying static code"
    exitSuccessfully    
  fi 
  buildKameHouseProject
  if ${DEPLOY_TO_TOMCAT}; then
    executeOperationInTomcatManager "stop" ${TOMCAT_PORT} ${MODULE_SHORT}
    executeOperationInTomcatManager "undeploy" ${TOMCAT_PORT} ${MODULE_SHORT}
    deployToTomcat
  fi
  deployKameHouseCmd
  cleanUpMavenRepository
}

setGlobalVariables() {
  WORKSPACE=${HOME}/workspace-${IDE}
  PROJECT_DIR=${WORKSPACE}/kamehouse
  DEPLOYMENT_DIR=${HOME}/programs/apache-tomcat-dev/webapps
  if ${IS_LINUX_HOST}; then
    source ${HOME}/programs/kamehouse-shell/bin/lin/bashrc/java-home.sh
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

# Get kamehouse httpd content root directory
getHttpdContentRoot() {
  if ${IS_LINUX_HOST}; then
    echo "/var/www/www-${IDE}"  
  else
    echo "${HOME}/programs/apache-httpd/www/www-${IDE}"
  fi
}

deployKameHouseMobileStatic() {
  if [[ "${MODULE}" == "kamehouse-mobile" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-mobile static content${COL_DEFAULT_LOG}"
    local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
    rm -rf ${HTTPD_CONTENT_ROOT}/kame-house-mobile
    mkdir -p ${HTTPD_CONTENT_ROOT}/kame-house-mobile
    cp -rf ./kamehouse-mobile/www/kame-house-mobile/* ${HTTPD_CONTENT_ROOT}/kame-house-mobile/
    checkCommandStatus "$?" "An error occurred deploying kamehouse mobile static content"

    local FILES=`find ${HTTPD_CONTENT_ROOT}/kame-house-mobile -name '.*' -prune -o -type f`
    while read FILE; do
      if [ -n "${FILE}" ]; then
        chmod a+rx ${FILE}
      fi
    done <<< ${FILES}

    local DIRECTORIES=`find ${HTTPD_CONTENT_ROOT}/kame-house-mobile -name '.*' -prune -o -type d`
    while read DIRECTORY; do
      if [ -n "${DIRECTORY}" ]; then
        chmod a+rx ${DIRECTORY}
      fi
    done <<< ${DIRECTORIES}

    log.info "Finished deploying ${COL_PURPLE}kamehouse-mobile static content${COL_DEFAULT_LOG}"
  fi
}

parseArguments() {
  parseIde "$@"
  parseKameHouseModule "$@"
  
  while getopts ":di:m:s" OPT; do
    case $OPT in
    ("d")
      DEPLOY_TO_DOCKER=true
      ;;
    ("s")
      STATIC_ONLY=true
      ;;   
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  setEnvForIde
  setEnvForKameHouseModule
}

printHelpOptions() {
  addHelpOption "-d" "deploy to docker"
  printIdeOption "ide's tomcat to deploy to"
  printKameHouseModuleOption "deploy"
  addHelpOption "-s" "deploy static ui code only"
}

main "$@"
