#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  log.info "Creating sample video playlists"
  if [ -d "${HOME}/git/kamehouse-video-playlists/.git" ]; then
    log.error "${HOME}/git/kamehouse-video-playlists is a git repository. No need to create sample playlists. Exiting..."
    exitProcess ${EXIT_ERROR}
  fi
  createPlaylists
  updateMediaFiles
  updatePlaylistsPath
}

createPlaylists() {
  log.info "Creating remote playlists"
  rm -r ${HOME}/git/kamehouse-video-playlists/playlists/video-kamehouse/
  mkdir -p ${HOME}/git/kamehouse-video-playlists/playlists/video-kamehouse/
  cp -rvf ${HOME}/git/kamehouse/docker/media/playlist/* ${HOME}/git/kamehouse-video-playlists/playlists/video-kamehouse/
}

updateMediaFiles() {
  log.info "Updating media files"
  rm -r ${HOME}/docker/media/video
  mkdir -p ${HOME}/docker/media/video
  cp -r ${HOME}/git/kamehouse/docker/media/video ${HOME}/docker/media/
}

updatePlaylistsPath() {
  log.info "Updating path with current user in remote playlists entries"
  cd ${HOME}/git/kamehouse-video-playlists/playlists/video-kamehouse/
  local USERNAME=`whoami`
  find . -regex ".*m3u" -type f -exec sed -i "s#/home/USERNAME#/home/${USERNAME}#g" {} \;
}

main "$@"
