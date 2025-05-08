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

mainProcess() {
  deployKameHouseProject
}

setInitialGlobalEnv() {
  EXIT_CODE=${EXIT_SUCCESS}
  DEPLOYMENT_DIR=""
  FAST_BUILD=true
  DEPLOY_TO_TOMCAT=false
  STATIC_ONLY=false
  LOG_LEVEL=TRACE

  USE_CURRENT_DIR=true
  TOMCAT_DIR="${TOMCAT_DIR_DEV}"
  TOMCAT_PORT=${DEFAULT_TOMCAT_DEV_PORT}
}

deployKameHouseMobile() {
  if [[ "${MODULE}" == "kamehouse-mobile" ]]; then
    if [ -f "${KAMEHOUSE_ANDROID_APK_PATH}" ]; then
      uploadKameHouseMobileApkToGDrive
      ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-upload-apk-to-device.sh -s
    else
      log.error "${KAMEHOUSE_ANDROID_APK_PATH} not found. Was the build successful?"
      EXIT_CODE=${EXIT_ERROR}
    fi
  fi
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
  printKameHouseModuleOption "deploy"
  printMavenProfileOption
  addHelpOption "-s" "deploy static ui code only"
}

main "$@"
