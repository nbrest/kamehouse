#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi
source ${HOME}/my.scripts/common/audio-playlists/audio-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing audio-playlists-functions.sh\033[0;39m"
  exit 1
fi

PATH_SD_CARD_MUSIC="/storage/0000-0000/Music"
PATH_PLS_DEST=${HOME}/git/texts/audio_playlists/android
PATH_BASE_DEST="${PATH_SD_CARD_MUSIC}/mp3"
PATH_BASE_N2_DEST="${PATH_SD_CARD_MUSIC}/truecrypt/mp3"

main "$@"
