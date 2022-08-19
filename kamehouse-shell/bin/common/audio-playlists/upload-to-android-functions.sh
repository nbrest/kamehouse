LOG_PROCESS_TO_FILE=true
ANDROID_IP="192.168.0.92"
ANDROID_2_IP="192.168.0.93"
ANDROID_PORT=2222
SFTP_USER=android
ANDROID_PHONE_NAME="android-1"

mainProcess() {    
  log.warn "DEPRECATED. Instead of using this script, use MGit app on my phone and git pull the playlists repo from my phone"
  exit 1

  log.warn "Start SSH/SFTP Server - Terminal on the android phone before proceeding"
  log.warn "The server should be configured as specified in audio-playlists.md"
  log.info "Uploading playlists to android phone. pass: android"
  sftp -P ${ANDROID_PORT} ${SFTP_USER}@${ANDROID_IP} <<< "put ${ANDROID_PLAYLISTS_PATH}/*.m3u ${SD_CARD_PLAYLISTS_PATH}/" 
}

parseArguments() {
  while getopts ":i:n:p:" OPT; do
    case $OPT in
    ("n")
      ANDROID_PHONE_NAME=$OPTARG
      ;;
    ("i")
      ANDROID_IP=$OPTARG
      ;;
    ("p")
      ANDROID_PORT=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  if [ "${ANDROID_PHONE_NAME}" != "android-1" ] &&
    [ "${ANDROID_PHONE_NAME}" != "android-2" ]; then
    log.error "Option -n [phone name] has an invalid value of ${ANDROID_PHONE_NAME}"
    printHelp
    exitProcess 1
  fi
  
  if [ "${ANDROID_PHONE_NAME}" == "android-2" ]; then
    ANDROID_IP=${ANDROID_2_IP}
  fi 
}

printHelpOptions() {
  addHelpOption "-n [android-1|android-2]" "android phone to sync to. default is android-1"
  addHelpOption "-i [ip]" "android sftp server ip"
  addHelpOption "-p [port]" "android sftp server port"
}