#!/bin/bash

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

DOCKER_CONTAINER_USERNAME=`ls /home | grep -v "nbrest"`

. /home/${DOCKER_CONTAINER_USERNAME}/.env

main() {
  log.info "Creating sample video playlists"
  if [ -d "/home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse-video-playlists/.git" ]; then
    log.info "/home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse-video-playlists is a git repository. No need to create sample playlists. Exiting..."
    exit ${EXIT_ERROR}
  fi
  createPlaylists
  updatePlaylistEntriesHome
  createOldPlaylistsDirs
}

createPlaylists() {
  log.info "Creating playlists directories"
  rm -r /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse-video-playlists/playlists/http-media-server-ip/media-drive/
  mkdir -p /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse-video-playlists/playlists/http-media-server-ip/media-drive/
  cp -vr /home/${DOCKER_CONTAINER_USERNAME}/docker/media/playlist/* /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse-video-playlists/playlists/http-media-server-ip/media-drive/
}

updatePlaylistEntriesHome() {
  log.info "Updating home path in playlist entries"
  cd /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse-video-playlists/playlists/http-media-server-ip/media-drive/
  find . -regex ".*m3u" -type f -exec sed -i "s#/home/USERNAME#/home/${DOCKER_CONTAINER_USERNAME}#g" {} \;
}

createOldPlaylistsDirs() {
  log.info "Creating old playlists dirs"
  mkdir -p /home/${DOCKER_CONTAINER_USERNAME}/git/texts/video_playlists/linux/niko4tbusb
  cp -rf /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse-video-playlists/playlists/http-media-server-ip/media-drive/* /home/${DOCKER_CONTAINER_USERNAME}/git/texts/video_playlists/linux/niko4tbusb/
  mkdir -p /home/${DOCKER_CONTAINER_USERNAME}/git/texts/video_playlists/http-niko-server/media-drive
  cp -rf /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse-video-playlists/playlists/http-media-server-ip/media-drive/* /home/${DOCKER_CONTAINER_USERNAME}/git/texts/video_playlists/http-niko-server/media-drive/
}

log.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"
