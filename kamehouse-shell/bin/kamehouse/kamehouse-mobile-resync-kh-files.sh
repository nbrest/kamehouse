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

PROJECT_DIR=
EXPORT_KAMEHOUSE_DIR=
GIT_REPO_SOURCE="dev"
DELETE_ONLY=false

mainProcess() {
  setGlobalVariables
  exportKameHouseUi
  exportGroot
  exportMockedApis
}

setGlobalVariables() {
  CURRENT_DIR=`pwd`
  WORKSPACE=${HOME}/workspace-${IDE}
  PROJECT_DIR=${WORKSPACE}/kamehouse
  if [ "${GIT_REPO_SOURCE}" == "prod" ]; then
    PROJECT_DIR=${HOME}/git/kamehouse
  fi
  if [ "${CURRENT_DIR}" == "${HOME}/git/jenkins/kamehouse" ]; then
    PROJECT_DIR=${HOME}/git/jenkins/kamehouse
  fi
  log.info "Using directory ${COL_PURPLE}${PROJECT_DIR}"
  SOURCE_FILES_KAMEHOUSE_DIR=${PROJECT_DIR}/kamehouse-ui/src/main/webapp
  SOURCE_FILES_GROOT_DIR=${PROJECT_DIR}/kamehouse-groot/public/kame-house-groot

  EXPORT_KAMEHOUSE_DIR=${PROJECT_DIR}/kamehouse-mobile/www/kame-house
  EXPORT_GROOT_DIR=${PROJECT_DIR}/kamehouse-mobile/www/kame-house-groot
  EXPORT_MOCKED_APIS_DIR=${PROJECT_DIR}/kamehouse-mobile/www

  MOCKED_KAMEHOUSE_API_DIR="${PROJECT_DIR}/kamehouse-mobile/apis"
}

exportKameHouseUi() {
  log.debug "Using SOURCE_FILES_KAMEHOUSE_DIR = ${SOURCE_FILES_KAMEHOUSE_DIR}"
  log.debug "Using EXPORT_KAMEHOUSE_DIR = ${EXPORT_KAMEHOUSE_DIR}"
  log.info "Deleting existing files from target dir ${EXPORT_KAMEHOUSE_DIR}"
  rm -r -f ${EXPORT_KAMEHOUSE_DIR}
  if ${DELETE_ONLY}; then
    log.debug "Running with -d. Skip resyncing kamehouse ui files to mobile app"
    return
  fi

  mkdir -p ${EXPORT_KAMEHOUSE_DIR}
  
  log.info "Copying all files from ${SOURCE_FILES_KAMEHOUSE_DIR} to ${EXPORT_KAMEHOUSE_DIR}"
  cd ${EXPORT_KAMEHOUSE_DIR}
  cp -r ${SOURCE_FILES_KAMEHOUSE_DIR}/* .

  log.debug "Removing WEB-INF folder from ${EXPORT_KAMEHOUSE_DIR}"
  rm -r ${EXPORT_KAMEHOUSE_DIR}/WEB-INF
}

exportGroot() {
  log.info "Deleting existing files from target dir ${EXPORT_GROOT_DIR}"
  rm -r -f ${EXPORT_GROOT_DIR}
  if ${DELETE_ONLY}; then
    log.debug "Running with -d. Skip resyncing groot files to mobile app"
    return
  fi
  log.info "Copying all files from ${SOURCE_FILES_GROOT_DIR} to ${EXPORT_GROOT_DIR}"
  mkdir -p ${EXPORT_GROOT_DIR}
  cd ${EXPORT_GROOT_DIR}
  cp -r ${SOURCE_FILES_GROOT_DIR}/* .

  log.debug "Moving groot php files to html from kamehouse groot for mobile app and removing php tags"
  local PHP_FILES=`find ./admin | grep -e ".php"`;
  while read PHP_FILE; do
    local HTML_FILE=${PHP_FILE::-3}html
    mv ${PHP_FILE} ${HTML_FILE}
    sed -i "s#<?php.*?>##Ig" "${HTML_FILE}"
  done <<< ${PHP_FILES}
}

exportMockedApis() {
  log.debug "Copying mocked localhost apis"
  cp -r -f ${MOCKED_KAMEHOUSE_API_DIR}/* ${EXPORT_MOCKED_APIS_DIR}/
}

parseArguments() {
  parseIde "$@"

  while getopts ":di:s:" OPT; do
    case $OPT in
    ("d")
      DELETE_ONLY=true
      ;;
    ("s")
      setGitRepoSource "$OPTARG"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setGitRepoSource() {
  local SOURCE_ARG=$1 
  SOURCE_ARG=`echo "${SOURCE_ARG}" | tr '[:upper:]' '[:lower:]'`

  if [ "${SOURCE_ARG}" != "prod" ] \
      && [ "${SOURCE_ARG}" != "dev" ]; then
    log.error "Option -s git source has an invalid value of ${SOURCE_ARG}"
    printHelp
    exitProcess 1
  fi
        
  GIT_REPO_SOURCE=${SOURCE_ARG}
}

setEnvFromArguments() {
  setEnvForIde
}

printHelpOptions() {
  addHelpOption "-d" "only delete /kame-house folder from mobile app folder. don't resync"
  printIdeOption "ide's path to export scripts to. Default is ${DEFAULT_IDE}"
  addHelpOption "-s (prod|dev)" "git repo to use as source. default is dev. use prod when calling from the deployment script"
}

main "$@"
