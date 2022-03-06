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

DEFAULT_DEV_ENVIRONMENT=intellij
# dev environment: eclipse or intellij
DEV_ENVIRONMENT=
PROJECT_DIR=
EXPORT_DIR=

mainProcess() {
  setGlobalVariables
  exportMyScripts
}

setGlobalVariables() {
  WORKSPACE=${HOME}/workspace-${DEV_ENVIRONMENT}
  PROJECT_DIR=${WORKSPACE}/java.web.kamehouse
  SOURCE_FILES_DIR=${PROJECT_DIR}/kamehouse-ui/src/main/webapp
  EXPORT_DIR=${PROJECT_DIR}/kamehouse-mobile/www/kame-house
}

exportMyScripts() {
  cd ${EXPORT_DIR}

  log.info "Deleting existing files from target dir"
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
  rm -r v ${EXPORT_DIR}/img/banners

  log.info "Copying /js files"
  cd ${EXPORT_DIR}
  cp -r -v ${SOURCE_FILES_DIR}/js/ .
  
  log.info "Copying /lib files"
  cd ${EXPORT_DIR}
  cp -r -v ${SOURCE_FILES_DIR}/lib/ .
}

parseArguments() {
  while getopts ":hi:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
    ("i")
      DEV_ENVIRONMENT=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
  
  if [ -z "${DEV_ENVIRONMENT}" ]; then
    log.warn "Option -i is not set. Using default value ${DEFAULT_DEV_ENVIRONMENT}"
    DEV_ENVIRONMENT=${DEFAULT_DEV_ENVIRONMENT}
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-i (eclipse|intellij)${COL_NORMAL} IDE's path to export scripts to.Default intellij" 
}

main "$@"
