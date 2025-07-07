#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Creating sample playlists"
  createPlaylists
  updateMediaFiles
  updatePlaylistsPath
}

createPlaylists() {
  log.info "Creating remote playlists"
  rm -r ${HOME}/.kamehouse/data/playlists
  mkdir -p ${HOME}/.kamehouse/data/playlists
  cp -rvf ${HOME}/git/kamehouse/docker/setup-kamehouse/media/playlist/* ${HOME}/.kamehouse/data/playlists/
}

updateMediaFiles() {
  log.info "Updating media files"
  rm -r ${HOME}/docker/setup-kamehouse/media/video
  mkdir -p ${HOME}/docker/setup-kamehouse/media/video
  cp -r ${HOME}/git/kamehouse/docker/setup-kamehouse/media/video ${HOME}/docker/setup-kamehouse/media/
}

updatePlaylistsPath() {
  log.info "Updating path with current user in remote playlists entries"
  cd ${HOME}/.kamehouse/data/playlists/
  local USERNAME=`whoami`
  find . -regex ".*m3u" -type f -exec sed -i "s#/home/USERNAME#/home/${USERNAME}#g" {} \;
}

main "$@"
