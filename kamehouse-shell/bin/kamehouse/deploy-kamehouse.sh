#!/bin/bash

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

LOAD_KAMEHOUSE_SECRETS=true
EXIT_CODE=${EXIT_SUCCESS}
DEPLOYMENT_DIR=""
FAST_BUILD=true
DEPLOY_TO_TOMCAT=false
STATIC_ONLY=false
LOG_LEVEL=INFO

mainProcess() {
  deployKameHouseProject
}

deployKameHouseMobileStatic() {
  log.debug "Skipping deploy kamehouse-mobile static code to httpd server (only done on dev deployment)"
}

parseArguments() {
  parseKameHouseModule "$@"
  parseMavenProfile "$@"

  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -m|-p)
        # parsed in a previous parse options function 
        ;;
      -c)
        USE_CURRENT_DIR=true
        ;;
      -l)
        LOG_LEVEL="${CURRENT_OPTION_ARG}"
        ;;  
      -s)
        STATIC_ONLY=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  setEnvForKameHouseModule
  setEnvForMavenProfile
}

printHelpOptions() {
  addHelpOption "-c" "deploy from the current directory without pulling latest git changes. Default deployment dir: ${COL_PURPLE}${PROJECT_DIR}"
  addHelpOption "-l [ERROR|WARN|INFO|DEBUG|TRACE]" "set log level for scripts. Default is INFO"
  printKameHouseModuleOption "deploy"
  printMavenProfileOption
  addHelpOption "-s" "deploy static ui code only"
}

main "$@"
