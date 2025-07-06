#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  SKIP_BUILD_MOBILE=false
  USE_CURRENT_DIR=true
}

mainProcess() {
  setKameHouseRootProjectDir
  log.info "Set ${COL_YELLOW}ANDROID_SFTP_IP, ANDROID_SFTP_PORT, ANDROID_SFTP_USERNAME, ANDROID_SFTP_APK_DEST_PATH${COL_DEFAULT_LOG} in ${HOME}/.kamehouse/config/kamehouse.cfg"
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
  log.info "Check pass in sftp server mobile app and store it in ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg as ANDROID_SFTP_PASS=password ${COL_PURPLE}to execute without password prompt when uploading from windows"

  SFTP_PORT="${ANDROID_SFTP_PORT}" 
  SFTP_USER="${ANDROID_SFTP_USERNAME}"
  SFTP_SERVER="${ANDROID_SFTP_IP}"
  SFTP_COMMAND="put ${KAMEHOUSE_ANDROID_APK_PATH} ${ANDROID_SFTP_APK_DEST_PATH}/kamehouse.apk"
  if ${IS_LINUX_HOST}; then 
    executeSftpCommand
  else
    log.warn "Putty pscp needs to be installed to send the apk to android device without password prompt"
    log.info "pscp -pw **** -v -P ${ANDROID_SFTP_PORT} ${KAMEHOUSE_ANDROID_APK_PATH} ${ANDROID_SFTP_USERNAME}@${ANDROID_SFTP_IP}:${ANDROID_SFTP_APK_DEST_PATH}/kamehouse.apk"
    pscp -pw ${ANDROID_SFTP_PASS} -v -P ${ANDROID_SFTP_PORT} ${KAMEHOUSE_ANDROID_APK_PATH} ${ANDROID_SFTP_USERNAME}@${ANDROID_SFTP_IP}:${ANDROID_SFTP_APK_DEST_PATH}/kamehouse.apk
    if [ "$?" != "0" ]; then
      log.error "Error using pscp. Falling back to sftp"
      executeSftpCommand
    fi
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
        ANDROID_SFTP_IP="${CURRENT_OPTION_ARG}"
        ;;
      -p)
        ANDROID_SFTP_PORT="${CURRENT_OPTION_ARG}"
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
