#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/programs/kamehouse-shell/bin/common/video-playlists/video-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing video-playlists-functions.sh\033[0;39m"
  exit 1
fi

PATH_PLS_SOURCE=${PROJECT_DIR}/http-media-server
PATH_PLS_DEST=http-media-server-vlc

mainProcess() {
  cd ${PROJECT_DIR}
  removeDestPlaylists
  createDestPlaylists
}

createDestPlaylists() {
  log.info "Creating http-media-server-vlc playlists"
  find ${PATH_PLS_SOURCE} | grep --ignore-case -e "\.m3u$" | while read FILE; do
    local PLAYLIST_RELATIVE_FILENAME=${FILE#${PATH_PLS_SOURCE}}
    local PLAYLIST_SUBDIR=${PLAYLIST_RELATIVE_FILENAME::-4}
    local PLAYLIST_FILE_NAME="$(basename "${PLAYLIST_RELATIVE_FILENAME}")"
    log.info "Copying playlist ${COL_PURPLE}${PLAYLIST_FILE_NAME}"
    mkdir -p "${PATH_PLS_DEST}${PLAYLIST_SUBDIR}"
    cp -f "${FILE}" "${PATH_PLS_DEST}${PLAYLIST_SUBDIR}/${PLAYLIST_FILE_NAME}"
  done
}

main "$@"
