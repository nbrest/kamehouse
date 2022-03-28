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

PATH_PLS_SOURCE=${PROJECT_DIR}/linux/
PATH_PLS_DEST=${PROJECT_DIR}/windows/

PATH_BASE_SOURCE="/media/media-drive"
PATH_BASE_DEST="N:"

replaceDestPath() {
  local FILE=$1
  log.info "Updating file ${COL_PURPLE}${FILE}"
  sed -i "s#${PATH_BASE_SOURCE}#${PATH_BASE_DEST}#Ig" ${FILE}
  checkCommandStatus "$?"
  sed -i "s#/#\\\#Ig" ${FILE}
  checkCommandStatus "$?"
}

main "$@"
