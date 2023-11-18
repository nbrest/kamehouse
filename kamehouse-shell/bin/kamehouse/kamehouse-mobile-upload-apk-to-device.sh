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
source ${HOME}/.kamehouse/.shell/.cred

ANDROID_IP="192.168.0.91"
ANDROID_PORT=2222
SKIP_BUILD_MOBILE=false
SD_CARD_APK_PATH=/0/Download
SFTP_USER=android

mainProcess() {
  if ${SKIP_BUILD_MOBILE}; then
    log.info "Running with -s. Skipping build kamehouse-mobile"
  else
    log.info "Building kamehouse-mobile app first"
    if ${REFRESH_CORDOVA_PLUGINS}; then
      ${HOME}/programs/kamehouse-shell/bin/kamehouse/build-kamehouse.sh -m mobile -b
    else
      ${HOME}/programs/kamehouse-shell/bin/kamehouse/build-kamehouse.sh -m mobile
    fi
  fi
  uploadApkToDeviceSftp
}

uploadApkToDeviceSftp() {
  setKameHouseMobileApkPath
  log.warn "${COL_PURPLE}Start SSH/SFTP Server - Terminal${COL_DEFAULT_LOG} on the android phone before proceeding"
  log.warn "The server should be configured as specified in ${COL_PURPLE}export-sync-audio-playlists.md"
  log.info "${COL_PURPLE}Uploading${COL_DEFAULT_LOG} kamehouse-mobile apk ${COL_PURPLE}to android device${COL_DEFAULT_LOG} through sftp"
  log.info "Check pass in sftp mobile app config and store it in ${HOME}/.kamehouse/.shell/.cred as ANDROID_SFTP_PASS=password ${COL_PURPLE}to execute without password prompt"
  if ${IS_LINUX_HOST}; then 
    log.debug "sftp -v -P ${ANDROID_PORT} ${SFTP_USER}@${ANDROID_IP} <<< \"put ${KAMEHOUSE_ANDROID_APK_PATH} ${SD_CARD_APK_PATH}/kamehouse.apk\" "
    sftp -v -P ${ANDROID_PORT} ${SFTP_USER}@${ANDROID_IP} <<< "put ${KAMEHOUSE_ANDROID_APK_PATH} ${SD_CARD_APK_PATH}/kamehouse.apk" 
  else
    log.warn "Putty pscp needs to be installed. if not switch to standard scp. Run with log=debug to see scp command"
    log.debug "sftp -v -P ${ANDROID_PORT} ${SFTP_USER}@${ANDROID_IP} <<< \"put ${KAMEHOUSE_ANDROID_APK_PATH} ${SD_CARD_APK_PATH}/kamehouse.apk\" "
    log.debug "pscp -pw [pass] -v -P ${ANDROID_PORT} ${KAMEHOUSE_ANDROID_APK_PATH} ${SFTP_USER}@${ANDROID_IP}:${SD_CARD_APK_PATH}/kamehouse.apk"
    pscp -pw ${ANDROID_SFTP_PASS} -v -P ${ANDROID_PORT} ${KAMEHOUSE_ANDROID_APK_PATH} ${SFTP_USER}@${ANDROID_IP}:${SD_CARD_APK_PATH}/kamehouse.apk
  fi
}

parseArguments() {
  while getopts ":i:p:rs" OPT; do
    case $OPT in
    ("i")
      ANDROID_IP=$OPTARG
      ;;
    ("p")
      ANDROID_PORT=$OPTARG
      ;;
    ("r")
      REFRESH_CORDOVA_PLUGINS=true
      ;;  
    ("s")
      SKIP_BUILD_MOBILE=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

printHelpOptions() {
  addHelpOption "-i [ip]" "android sftp server ip"
  addHelpOption "-p [port]" "android sftp server port"
  addHelpOption "-r" "refresh cordova plugins. disabled by default. ${COL_YELLOW}USE WITH CAUTION!!"
  addHelpOption "-s" "skip build kamehouse-mobile module before uploading. By default it rebuilds the apk"
}

main "$@"
