#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/.kamehouse/.shell/.cred

ANDROID_IP="192.168.0.92"
ANDROID_PORT=2222
ANDROID_APK=${HOME}/workspace-intellij/kamehouse/kamehouse-mobile/platforms/android/app/build/outputs/apk/debug/app-debug.apk
SKIP_BUILD_MOBILE=false
SD_CARD_APK_PATH=/0/Download
SFTP_USER=android

mainProcess() {
  if ${SKIP_BUILD_MOBILE}; then
    log.info "Running with -s. Skipping build kamehouse-mobile"
  else
    log.info "Building kamehouse-mobile app first"
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/build-kamehouse.sh -m mobile
  fi
  log.warn "Start SSH/SFTP Server - Terminal on the android phone before proceeding"
  log.warn "The server should be configured as specified in export-sync-audio-playlists.md"
  log.info "Uploading kamehouse mobile apk to android phone. pass ${COL_PURPLE}${ANDROID_SFTP_PASS}"
  if ${IS_LINUX_HOST}; then 
    log.debug "sftp -v -P ${ANDROID_PORT} ${SFTP_USER}@${ANDROID_IP} <<< \"put ${ANDROID_APK} ${SD_CARD_APK_PATH}/\" "
    sftp -v -P ${ANDROID_PORT} ${SFTP_USER}@${ANDROID_IP} <<< "put ${ANDROID_APK} ${SD_CARD_APK_PATH}/" 
  else
    log.warn "Putty pscp needs to be installed. if not switch to standard scp. Run with log=debug to see scp command"
    log.debug "sftp -v -P ${ANDROID_PORT} ${SFTP_USER}@${ANDROID_IP} <<< \"put ${ANDROID_APK} ${SD_CARD_APK_PATH}/\" "
    log.debug "pscp -pw [pass] -v -P ${ANDROID_PORT} ${ANDROID_APK} ${SFTP_USER}@${ANDROID_IP}:${SD_CARD_APK_PATH}/"
    pscp -pw ${ANDROID_SFTP_PASS} -v -P ${ANDROID_PORT} ${ANDROID_APK} ${SFTP_USER}@${ANDROID_IP}:${SD_CARD_APK_PATH}/
  fi
}

parseArguments() {
  while getopts ":i:p:s" OPT; do
    case $OPT in
    ("i")
      ANDROID_IP=$OPTARG
      ;;
    ("p")
      ANDROID_PORT=$OPTARG
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
  addHelpOption "-s" "skip build kamehouse-mobile module before uploading. By default it rebuilds the apk"
}

main "$@"
