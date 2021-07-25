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

MODULE=
KAMEHOUSE_CMD_DEPLOY_PATH="${HOME}/programs"

mainProcess() {
  buildProject
  deployKameHouseCmd
  cleanLogsInGitRepoFolder
}

buildProject() {
  if [ -n "${MODULE}" ]; then
    log.info "Building module ${COL_PURPLE}${MODULE}"
    mvn clean install -pl :${MODULE} -am
  else
    log.info "Building all modules"
    mvn clean install
  fi
  checkCommandStatus "$?" "An error occurred building the kamehouse"
}

deployKameHouseCmd() {
  if [[ -z "${MODULE}" || "${MODULE}" == "kamehouse-cmd" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-cmd${COL_DEFAULT_LOG} to ${COL_PURPLE}${KAMEHOUSE_CMD_DEPLOY_PATH}${COL_DEFAULT_LOG}"
    mkdir -p ${KAMEHOUSE_CMD_DEPLOY_PATH}
    unzip kamehouse-cmd/target/kamehouse-cmd-bundle.zip -d ${KAMEHOUSE_CMD_DEPLOY_PATH}/ 
  fi
}

parseArguments() {
  while getopts ":hm:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
    ("m")
      MODULE="kamehouse-$OPTARG"
      ;;  
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
  
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-m (admin|cmd|media|tennisworld|testmodule|ui|vlcrc)${COL_NORMAL} module to build"
  echo -e "     ${COL_BLUE}-p (prod|qa|dev)${COL_NORMAL} maven profile to build the project with. Default is prod if not specified"
}

main "$@"
