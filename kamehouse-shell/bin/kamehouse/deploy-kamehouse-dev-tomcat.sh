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

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 1
fi

source ${HOME}/.kamehouse/.shell/.cred

DEPLOYMENT_DIR=
TOMCAT_PORT=${DEFAULT_TOMCAT_DEV_PORT}
FAST_BUILD=true

mainProcess() {
  setGlobalVariables
  setKameHouseRootProjectDir
  buildKameHouseProject
  executeOperationInTomcatManager "stop" ${TOMCAT_PORT} ${MODULE_SHORT}
  executeOperationInTomcatManager "undeploy" ${TOMCAT_PORT} ${MODULE_SHORT}
  deployToTomcat
}

setGlobalVariables() {
  WORKSPACE=${HOME}/workspace-${IDE}
  PROJECT_DIR=${WORKSPACE}/kamehouse
  DEPLOYMENT_DIR=${HOME}/programs/apache-tomcat-dev/webapps
  if ${IS_LINUX_HOST}; then
    source ${HOME}/programs/kamehouse-shell/bin/lin/bashrc/java-home.sh
  fi
}

parseArguments() {
  parseIde "$@"
  parseKameHouseModule "$@"
  
  while getopts ":di:m:" OPT; do
    case $OPT in
    ("d")
      DEPLOY_TO_DOCKER=true
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
}

main "$@"
