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

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/build-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing build-functions.sh\033[0;39m"
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
LOG_LEVEL=INFO

RESET_PACKAGE_JSON=true

mainProcess() {
  deployKameHouseProject
}

deployKameHouseMobileStatic() {
  log.debug "Skipping deploy kamehouse-mobile static code"
}

parseArguments() {
  parseKameHouseModule "$@"
  parseMavenProfile "$@"

  while getopts ":bcm:l:p:s" OPT; do
    case $OPT in
    ("b")
      REFRESH_CORDOVA_PLUGINS=true
      ;;
    ("c")
      USE_CURRENT_DIR=true
      ;;
    ("l")
      LOG_LEVEL=$OPTARG
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
  setEnvForMavenProfile
}

printHelpOptions() {
  addHelpOption "-b" "mobile: refresh cordova plugins ${COL_YELLOW}USE WHEN VERY SURE"
  addHelpOption "-c" "deploy current version of the current directory without pulling latest version. Default deployment dir: ${PROJECT_DIR}"
  addHelpOption "-l [ERROR|WARN|INFO|DEBUG|TRACE]" "set log level for scripts. Default is INFO"
  printKameHouseModuleOption "deploy"
  printMavenProfileOption
  addHelpOption "-s" "deploy static ui code only"
}

main "$@"
