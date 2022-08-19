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

DEFAULT_DEV_ENVIRONMENT=intellij
# dev environment: eclipse or intellij
DEV_ENVIRONMENT=
PROJECT_DIR=
EXPORT_DIR=
PROFILE="dev"

mainProcess() {
  setGlobalVariables
  exportWebapp
}

setGlobalVariables() {
  WORKSPACE=${HOME}/workspace-${DEV_ENVIRONMENT}
  PROJECT_DIR=${WORKSPACE}/kamehouse
  if [ "${PROFILE}" == "prod" ]; then
    PROJECT_DIR=${HOME}/git/kamehouse
  fi
  SOURCE_FILES_DIR=${PROJECT_DIR}/kamehouse-ui/src/main/webapp
  EXPORT_DIR=${PROJECT_DIR}/kamehouse-mobile/www/kame-house
}

exportWebapp() {
  cd ${EXPORT_DIR}

  log.info "Using SOURCE_FILES_DIR = ${SOURCE_FILES_DIR}"
  log.info "Using EXPORT_DIR = ${EXPORT_DIR}"
  log.info "Deleting existing files from target dir ${EXPORT_DIR}"
  rm -r -v -f ${EXPORT_DIR}
  mkdir -p ${EXPORT_DIR}

  log.info "Copying /css files"
  cd ${EXPORT_DIR}
  cp -r -v ${SOURCE_FILES_DIR}/css/ .

  log.info "Copying /html-snippets files"
  cd ${EXPORT_DIR}
  cp -r -v ${SOURCE_FILES_DIR}/html-snippets/ .

  log.info "Copying /img files"
  cd ${EXPORT_DIR}
  cp -r -v ${SOURCE_FILES_DIR}/img/ .

  log.info "Copying /js files"
  cd ${EXPORT_DIR}
  cp -r -v ${SOURCE_FILES_DIR}/js/ .
  
  log.info "Copying /lib files"
  cd ${EXPORT_DIR}
  cp -r -v ${SOURCE_FILES_DIR}/lib/ .
}

parseArguments() {
  while getopts ":i:p:" OPT; do
    case $OPT in
    ("i")
      DEV_ENVIRONMENT=$OPTARG
      ;;
    ("p")
      local PROFILE_ARG=$OPTARG 
      PROFILE_ARG=`echo "${PROFILE_ARG}" | tr '[:upper:]' '[:lower:]'`
      
      if [ "${PROFILE_ARG}" != "prod" ] \
          && [ "${PROFILE_ARG}" != "dev" ]; then
        log.error "Option -p profile has an invalid value of ${PROFILE_ARG}"
        printHelp
        exitProcess 1
      fi
            
      PROFILE=${PROFILE_ARG}
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  if [ -z "${DEV_ENVIRONMENT}" ]; then
    log.info "Option -i is not set. Using default value ${DEFAULT_DEV_ENVIRONMENT}"
    DEV_ENVIRONMENT=${DEFAULT_DEV_ENVIRONMENT}
  fi  
}

printHelpOptions() {
  addHelpOption "-i ${IDE_LIST}" "IDE's path to export scripts to.Default intellij"
  addHelpOption "-p (prod|dev)" "environment to deploy. default is dev. use prod when calling from the deployment script"
}

main "$@"
