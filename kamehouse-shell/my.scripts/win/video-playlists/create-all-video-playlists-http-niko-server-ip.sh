#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/my.scripts/common/video-playlists/video-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing video-playlists-functions.sh\033[0;39m"
  exit 1
fi

PATH_PLS_SOURCE=${HOME}/git/texts/video_playlists/http-niko-server
PATH_PLS_DEST=${HOME}/git/texts/video_playlists/http-niko-server-ip

PATH_BASE_SOURCE="http:\/\/niko-server\/kame-house-streaming\/media-server\/media-drive"
PATH_BASE_DEST="http:\/\/192.168.0.129\/kame-house-streaming\/media-server\/media-drive"

mainProcess() {
  validateVariables
  removeDestPlaylists
  copySourcePlaylists
  replaceDestPaths
}

main "$@"
