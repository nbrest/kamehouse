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

LOG_PROCESS_TO_FILE=true
KAMEHOUSE_MOBILE_APP_PATH="/var/www/kamehouse-webserver/kame-house-mobile"
KAMEHOUSE_APK_HTML_TEMPLATE=${HOME}/programs/kamehouse-shell/conf/kamehouse-apk-template.html
KAMEHOUSE_APK_HTML=kamehouse-apk.html
GIT_COMMIT_HASH=""

mainProcess() {
  log.info "Re generating apk html file"
  cd ${KAMEHOUSE_MOBILE_APP_PATH}

  log.info "Copying html from template"
  cp ${KAMEHOUSE_APK_HTML_TEMPLATE} ${KAMEHOUSE_APK_HTML}

  log.info "Updating hash"
  local SHA_HASH=`sha256sum kamehouse.apk`
  sed -i "s#-----SHA_HASH-----#${SHA_HASH}#I" "${KAMEHOUSE_APK_HTML}"

  log.info "Updating git commit"
  sed -i "s#-----GIT_COMMIT_HASH-----#${GIT_COMMIT_HASH}#I" "${KAMEHOUSE_APK_HTML}"

  log.info "Updating apk files"
  local APK_FILES=`ls -ln | grep -v ".html" | grep ".apk" | cut -d ' ' -f 5-`
  APK_FILES=`echo -e "${APK_FILES}"`
  sed -i "s#-----APK_FILES-----#`echo ${APK_FILES}`#I" "${KAMEHOUSE_APK_HTML}"
}

parseArguments() {
  while getopts ":c:" OPT; do
    case $OPT in
    ("c")
      GIT_COMMIT_HASH=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  if [ -z "${GIT_COMMIT_HASH}" ]; then
    log.error "git commit hash not passed with argument -c"
    exitProcess 1
  fi  
}

printHelpOptions() {
  addHelpOption "-c hash" "git commit hash"
}

main "$@"
