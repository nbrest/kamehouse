#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi
loadKamehouseShellPwd

SKIP_BUILD_MOBILE=false
USE_CURRENT_DIR=true

mainProcess() {
  setKameHouseRootProjectDir
  log.info "Set ${COL_RED}ANDROID_IP, ANDROID_PORT, ANDROID_SFTP_USER, ANDROID_APK_DEST_PATH${COL_DEFAULT_LOG} in ${HOME}/.kamehouse/config/kamehouse.cfg"
  if ${SKIP_BUILD_MOBILE}; then
    log.info "Running with -s. Skipping build kamehouse-mobile"
  else
    log.info "Building kamehouse-mobile app first"
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/build-kamehouse.sh -m mobile
  fi
  uploadApkToDeviceSftp
}

uploadApkToDeviceSftp() {
  setKameHouseMobileApkPath
  log.warn "${COL_PURPLE}Start SSH/SFTP Server - Terminal${COL_DEFAULT_LOG} on the android phone before proceeding"
  log.info "${COL_PURPLE}Uploading${COL_DEFAULT_LOG} kamehouse-mobile apk ${COL_PURPLE}to android device${COL_DEFAULT_LOG} through sftp"
  log.info "Check pass in sftp mobile app config and store it in ${HOME}/.kamehouse/config/.shell/shell.pwd as ANDROID_SFTP_PASS=password ${COL_PURPLE}to execute without password prompt"
  if ${IS_LINUX_HOST}; then 
    log.debug "sftp -v -P ${ANDROID_PORT} ${ANDROID_SFTP_USER}@${ANDROID_IP} <<< \"put ${KAMEHOUSE_ANDROID_APK_PATH} ${ANDROID_APK_DEST_PATH}/kamehouse.apk\" "
    sftp -v -P ${ANDROID_PORT} ${ANDROID_SFTP_USER}@${ANDROID_IP} <<< "put ${KAMEHOUSE_ANDROID_APK_PATH} ${ANDROID_APK_DEST_PATH}/kamehouse.apk" 
  else
    log.warn "Putty pscp needs to be installed. if not switch to standard scp. Run with log=debug to see scp command"
    log.debug "sftp -v -P ${ANDROID_PORT} ${ANDROID_SFTP_USER}@${ANDROID_IP} <<< \"put ${KAMEHOUSE_ANDROID_APK_PATH} ${ANDROID_APK_DEST_PATH}/kamehouse.apk\" "
    log.debug "pscp -pw [pass] -v -P ${ANDROID_PORT} ${KAMEHOUSE_ANDROID_APK_PATH} ${ANDROID_SFTP_USER}@${ANDROID_IP}:${ANDROID_APK_DEST_PATH}/kamehouse.apk"
    pscp -pw ${ANDROID_SFTP_PASS} -v -P ${ANDROID_PORT} ${KAMEHOUSE_ANDROID_APK_PATH} ${ANDROID_SFTP_USER}@${ANDROID_IP}:${ANDROID_APK_DEST_PATH}/kamehouse.apk
  fi
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -i)
        ANDROID_IP="${CURRENT_OPTION_ARG}"
        ;;
      -p)
        ANDROID_PORT="${CURRENT_OPTION_ARG}"
        ;;
      -s)
        SKIP_BUILD_MOBILE=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
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
