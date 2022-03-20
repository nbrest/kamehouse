#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

ANDROID_IP="192.168.0.92"
ANDROID_PORT=2222
ANDROID_PLAYLISTS_PATH=${HOME}/git/texts/audio_playlists/android-internal
SD_CARD_PLAYLISTS_PATH=/0/Playlists
SFTP_USER=android

mainProcess() {
  log.warn "Start SSH/SFTP Server - Terminal on the android phone before proceeding"
  log.warn "The server should be configured as specified in export-sync-audio-playlists.md"
  log.info "Uploading playlists to android phone. pass: android"
  sftp -P ${ANDROID_PORT} ${SFTP_USER}@${ANDROID_IP} <<< "put ${ANDROID_PLAYLISTS_PATH}/*.m3u ${SD_CARD_PLAYLISTS_PATH}/" 
}

parseArguments() {
  while getopts ":hi:p:" OPT; do
    case $OPT in
    ("h")
      parseHelp
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

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-i [ip]${COL_NORMAL} android sftp server ip"
  echo -e "     ${COL_BLUE}-p [port]${COL_NORMAL} android sftp server port"
}

main "$@"
