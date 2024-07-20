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

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/deployment-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing deployment-functions.sh\033[0;39m"
  exit 99
fi
source ${HOME}/.kamehouse/.shell/.cred

EXIT_CODE=${EXIT_SUCCESS}
DEPLOYMENT_DIR=""
FAST_BUILD=true
DEPLOY_TO_TOMCAT=false
STATIC_ONLY=false
LOG_LEVEL=TRACE

USE_CURRENT_DIR=true
TOMCAT_DIR="${TOMCAT_DIR_DEV}"
TOMCAT_PORT=${DEFAULT_TOMCAT_DEV_PORT}

mainProcess() {
  deployKameHouseProject
}

deployKameHouseMobile() {
  log.debug "Skipping deploy kamehouse-mobile for dev deployment"
}

# Get kamehouse httpd content root directory
getHttpdContentRoot() {
  if ${IS_LINUX_HOST}; then
    echo "/var/www/kamehouse-webserver-dev"  
  else
    echo "${HOME}/programs/apache-httpd/www/kamehouse-webserver-dev"
  fi
}

parseArguments() {
  parseKameHouseModule "$@"
  
  while getopts ":dm:s" OPT; do
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
  setEnvForKameHouseModule
}

printHelpOptions() {
  addHelpOption "-d" "deploy to docker"
  printKameHouseModuleOption "deploy"
  addHelpOption "-s" "deploy static ui code only"
}

main "$@"
