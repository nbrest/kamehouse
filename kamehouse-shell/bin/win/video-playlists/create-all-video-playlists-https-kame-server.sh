#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi
source ${HOME}/programs/kamehouse-shell/bin/common/functions/video-playlists/video-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing video-playlists-functions.sh\033[0;39m"
  exit 99
fi

PATH_PLS_SOURCE=${PROJECT_DIR}/http-media-server
PATH_PLS_DEST=${PROJECT_DIR}/https-kame-server

PATH_BASE_SOURCE="http://${MEDIA_SERVER}/kame-house-streaming/media-server/media-drive"
PATH_BASE_DEST="https://kame.nicobrest.com/kame-house-streaming/media-server/media-drive"

main "$@"
