#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 149
fi
source ${HOME}/programs/kamehouse-shell/bin/common/video-playlists/video-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing video-playlists-functions.sh\033[0;39m"
  exit 149
fi

PATH_PLS_SOURCE=${PROJECT_DIR}/http-media-server-vlc
PATH_PLS_DEST=${PROJECT_DIR}/smb-media-server-vlc

PATH_BASE_SOURCE="http://${MEDIA_SERVER_IP}/kame-house-streaming/media-server/media-drive/"
PATH_BASE_DEST="smb://${MEDIA_SERVER_USERNAME}@${MEDIA_SERVER_IP}/media-drive/"

main "$@"
