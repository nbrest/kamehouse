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
  TOMCAT_WEBAPPS_DIR=${WORKSPACE}/apache-tomcat-${TOMCAT_VERSION}/webapps
  EXPORT_DIR=${PROJECT_DIR}/kamehouse-shell
  DOCKER_DIR=${PROJECT_DIR}/docker/scripts
}

exportMyScripts() {
  cd ${EXPORT_DIR}

  log.info "Deleting existing scripts from workspace"
  rm -r -v -f ${EXPORT_DIR}/my.scripts
  mkdir -p ${EXPORT_DIR}/my.scripts
  rm -r -v -f ${DOCKER_DIR}
  mkdir -p ${DOCKER_DIR}

  log.info "Copying root scripts"
  cd ${EXPORT_DIR}/my.scripts
  cp -r -v ${HOME}/my.scripts/awk .
  cp -r -v ${HOME}/my.scripts/aws .
  cp -r -v ${HOME}/my.scripts/common .
  cp -r -v ${HOME}/my.scripts/kamehouse .
  cp -r -v ${HOME}/my.scripts/pi .
  cp -r -v ${HOME}/my.scripts/*.sh .

  log.info "Copying lin scripts"
  mkdir -p ${EXPORT_DIR}/my.scripts/lin
  cp -r -v ${HOME}/my.scripts/lin/bashrc lin/
  cp -r -v ${HOME}/my.scripts/lin/git lin/
  cp -r -v ${HOME}/my.scripts/lin/httpd lin/
  cp -r -v ${HOME}/my.scripts/lin/kamehouse lin/
  cp -r -v ${HOME}/my.scripts/lin/keep-alive lin/
  cp -r -v ${HOME}/my.scripts/lin/shutdown lin/
  cp -r -v ${HOME}/my.scripts/lin/sql lin/
  cp -r -v ${HOME}/my.scripts/lin/startup lin/
  cp -r -v ${HOME}/my.scripts/lin/sysadmin lin/
  cp -r -v ${HOME}/my.scripts/lin/transmission lin/
  cp -r -v ${HOME}/my.scripts/lin/vpn lin/
  cp -r -v ${HOME}/my.scripts/lin/*.sh lin/

  log.info "Copying win scripts"
  mkdir -p ${EXPORT_DIR}/my.scripts/win
  cp -r -v ${HOME}/my.scripts/win/audio-playlists win/
  cp -r -v ${HOME}/my.scripts/win/backup win/
  cp -r -v ${HOME}/my.scripts/win/bashrc win/
  cp -r -v ${HOME}/my.scripts/win/bat win/
  cp -r -v ${HOME}/my.scripts/win/git win/
  cp -r -v ${HOME}/my.scripts/win/httpd win/
  cp -r -v ${HOME}/my.scripts/win/kamehouse win/
  cp -r -v ${HOME}/my.scripts/win/keep-alive win/
  cp -r -v ${HOME}/my.scripts/win/shutdown win/
  cp -r -v ${HOME}/my.scripts/win/sql win/
  cp -r -v ${HOME}/my.scripts/win/sysadmin win/
  cp -r -v ${HOME}/my.scripts/win/video-playlists win/
  cp -r -v ${HOME}/my.scripts/win/virtualbox win/
  cp -r -v ${HOME}/my.scripts/win/*.sh win/

  log.info "Copying docker scripts"
  cp -v ${HOME}/my.scripts/kamehouse/docker/* ${DOCKER_DIR}/

  log.info "Remove scripts that shouldn't be copied over"
  rm -v -f ${EXPORT_DIR}/my.scripts/test-script.sh
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
