#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi
source ${HOME}/programs/kamehouse-shell/bin/common/video-playlists/video-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing video-playlists-functions.sh\033[0;39m"
  exit 99
fi

PATH_PLS_SOURCE=${PROJECT_DIR}/linux
PATH_PLS_DEST=${PROJECT_DIR}/http-media-server

PATH_BASE_SOURCE="/media/media-drive"
PATH_BASE_DEST="http://${MEDIA_SERVER_IP}/kame-house-streaming/media-server/media-drive"

mainProcess() {
  validateVariables
  removeDestPlaylists
  copySourcePlaylists
  replaceDestPaths
  urlencodeDestPlaylists
}

urlencodeDestPlaylists() {
  log.info "Urlencode playlists in destination directory"
  PLAYLISTS_DEST=`find ${PATH_PLS_DEST} | grep -e  "m3u\|M3U" | sort`
  while read FILE; do
    urlencodeDestPlaylist ${FILE} &
  done < <(echo "${PLAYLISTS_DEST}")
  checkCommandStatus "$?"
  log.info "Waiting for all background urlencode processes to finish"
  jobs -l
  wait
}

urlencodeDestPlaylist() {
  local FILE=$1
  log.info "Urlencoding file ${COL_PURPLE}${FILE}"
  while read LINE; do
    LINE_START=${LINE:0:4}
    if [[ ${LINE_START} == "http" ]]; then
      URL_BASE=`echo "${LINE}" | cut -d "/" -f 1-5`
      FILENAME=`echo "${LINE}" | cut -d "/" -f 6-`
      ENCODED_URL=${URL_BASE}/$(urlencode "${FILENAME}")
      ENCODED_URL=${ENCODED_URL//%2F//}
      echo "${ENCODED_URL}" >> ${FILE}.tmp
    else 
      echo "${LINE}" >> ${FILE}.tmp
    fi
  done < ${FILE}
  mv ${FILE}.tmp ${FILE}  
}

main "$@"
