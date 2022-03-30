#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/programs/kamehouse-shell/bin/common/audio-playlists/upload-to-android-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing upload-to-android-functions.sh\033[0;39m"
  exit 1
fi

ANDROID_PLAYLISTS_PATH=${HOME}/git/kamehouse-audio-playlists/playlists/android-external
SD_CARD_PLAYLISTS_PATH=/0000-0000/Playlists

main "$@"
