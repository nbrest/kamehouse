#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 149
fi
# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 149
fi

LOG_PROCESS_TO_FILE=true
KAMEHOUSE_MOBILE_APP_PATH="/var/www/kamehouse-webserver/kame-house-mobile"
KAMEHOUSE_APK_HTML_TEMPLATE=${HOME}/programs/kamehouse-shell/conf/kamehouse-apk-template.html
KAMEHOUSE_APK_HTML=kamehouse-apk.html
BUILD_VERSION=""

mainProcess() {
  log.info "Re generating apk html file"
  cd ${KAMEHOUSE_MOBILE_APP_PATH}

  log.info "Copying html from template"
  cp ${KAMEHOUSE_APK_HTML_TEMPLATE} ${KAMEHOUSE_APK_HTML}

  log.info "Updating hash"
  local SHA_HASH=`sha256sum kamehouse.apk | awk '{print $1}'`
  sed -i "s#-----SHA_HASH-----#${SHA_HASH}#I" "${KAMEHOUSE_APK_HTML}"

  log.info "Updating build version"
  sed -i "s#-----BUILD_VERSION-----#${BUILD_VERSION}#I" "${KAMEHOUSE_APK_HTML}"

  log.info "Updating apk deploy date"
  local APK_DEPLOY_DATE=$(date +%Y-%m-%d' '%H:%M:%S)
  sed -i "s#-----APK_DEPLOY_DATE-----#${APK_DEPLOY_DATE}#I" "${KAMEHOUSE_APK_HTML}"
}

parseArguments() {
  while getopts ":b:" OPT; do
    case $OPT in
    ("b")
      BUILD_VERSION=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  if [ -z "${BUILD_VERSION}" ]; then
    log.error "build version not passed with argument -b"
    exitProcess 3
  fi  
}

printHelpOptions() {
  addHelpOption "-b v9.99.1-commit-hash" "build version"
}

main "$@"
