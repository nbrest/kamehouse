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

DELETE_ONLY=false
SOURCE_FILES_KAMEHOUSE_DIR=""
SOURCE_FILES_GROOT_DIR=""
SOURCE_FILES_BATCAVE_DIR=""
EXPORT_KAMEHOUSE_DIR=""
EXPORT_GROOT_DIR=""
EXPORT_BATCAVE_DIR=""
EXPORT_MOCKED_APIS_DIR=""
MOCKED_KAMEHOUSE_API_DIR=""

mainProcess() {
  setGlobalVariables
  exportKameHouseUi
  exportGroot
  exportBatcave
  exportMockedApis
}

setGlobalVariables() {
  setKameHouseRootProjectDir

  SOURCE_FILES_KAMEHOUSE_DIR=${PROJECT_DIR}/kamehouse-ui/src/main/webapp
  SOURCE_FILES_GROOT_DIR=${PROJECT_DIR}/kamehouse-groot/public/kame-house-groot
  SOURCE_FILES_BATCAVE_DIR="${HOME}/git/kamehouse-batcave/ui/public/kame-house-batcave"

  EXPORT_KAMEHOUSE_DIR=${PROJECT_DIR}/kamehouse-mobile/www/kame-house
  EXPORT_GROOT_DIR=${PROJECT_DIR}/kamehouse-mobile/www/kame-house-groot
  EXPORT_BATCAVE_DIR=${PROJECT_DIR}/kamehouse-mobile/www/kame-house-batcave
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
}

exportBatcave() {
  log.info "Deleting existing files from target dir ${EXPORT_BATCAVE_DIR}"
  rm -r -f ${EXPORT_BATCAVE_DIR}
  if ${DELETE_ONLY}; then
    log.debug "Running with -d. Skip resyncing batcave files to mobile app"
    return
  fi
  if [ -d "${SOURCE_FILES_BATCAVE_DIR}" ]; then
    log.info "Copying all files from ${SOURCE_FILES_BATCAVE_DIR} to ${EXPORT_BATCAVE_DIR}"
    cd ${SOURCE_FILES_BATCAVE_DIR}
    git pull origin dev
    mkdir -p ${EXPORT_BATCAVE_DIR}
    cd ${EXPORT_BATCAVE_DIR}
    cp -r ${SOURCE_FILES_BATCAVE_DIR}/* .
  fi
}

exportMockedApis() {
  log.info "Copying mocked localhost apis"
  cp -r -f ${MOCKED_KAMEHOUSE_API_DIR}/* ${EXPORT_MOCKED_APIS_DIR}/
}

parseArguments() {
  parseIde "$@"

  while getopts ":cdi:" OPT; do
    case $OPT in
    ("c")
      USE_CURRENT_DIR=true
      ;;
    ("d")
      DELETE_ONLY=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  setEnvForIde
}

printHelpOptions() {
  addHelpOption "-c" "use current directory to sync static files. Default dir: ${PROJECT_DIR}"
  addHelpOption "-d" "only delete /kame-house folder from mobile app folder. don't resync"
  printIdeOption "ide's path to export scripts to. Default is ${DEFAULT_IDE}"
}

main "$@"
