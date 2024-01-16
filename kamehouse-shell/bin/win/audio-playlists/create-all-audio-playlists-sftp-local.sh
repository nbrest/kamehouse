#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 99
fi
source ${HOME}/programs/kamehouse-shell/bin/common/functions/audio-playlists/audio-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing audio-playlists-functions.sh\033[0;39m"
  exit 99
fi

PATH_PLS_DEST=${PROJECT_DIR}/audio-sftp-local
PATH_BASE_DEST="sftp://${MEDIA_SERVER_USERNAME}@${MEDIA_SERVER_IP}/D:/niko9enzo/mp3"
PATH_BASE_N2_DEST="sftp://${MEDIA_SERVER_USERNAME}@${MEDIA_SERVER_IP}/Z:/mp3"

main "$@"
