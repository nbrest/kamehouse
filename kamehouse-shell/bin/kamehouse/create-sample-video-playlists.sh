#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=true

mainProcess() {
  log.info "Create sample video playlists"
  if [ -d "${HOME}/git/kamehouse-video-playlists/.git" ]; then
    log.warn "${HOME}/git/kamehouse-video-playlists is a git repository. No need to create sample playlists. Exiting..."
    exit 1
  fi
  mkdir -p ${HOME}/git/kamehouse-video-playlists/playlists/http-media-server-ip/media-drive/
  cp -vr ${HOME}/git/kamehouse/docker/media/playlist/* ${HOME}/git/kamehouse-video-playlists/playlists/http-media-server-ip/media-drive/
  rm -r ${HOME}/docker/media/video
  mkdir -p ${HOME}/docker/media/video
  cp -r ${HOME}/git/kamehouse/docker/media/video ${HOME}/docker/media/
}

main "$@"
