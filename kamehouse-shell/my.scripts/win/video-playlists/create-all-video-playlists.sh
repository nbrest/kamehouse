#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/my.scripts/.cred/.cred

LOG_PROCESS_TO_FILE=true
PROJECT_DIR="${HOME}/git/texts/video_playlists"
GIT_REMOTE=all
GIT_BRANCH=dev
MEDIA_SERVER="niko-server"

mainProcess() {
  checkMediaServer
  pullChangesFromGit
  deleteExistingM3uFiles

  ${HOME}/my.scripts/win/video-playlists/create-base-video-playlists-windows-bash.sh
  checkCommandStatus "$?" 
  
  ${HOME}/my.scripts/win/video-playlists/create-mix-video-playlists-windows-bash.sh
  checkCommandStatus "$?" 
  
  ${HOME}/my.scripts/win/video-playlists/create-all-video-playlists-linux.sh
  checkCommandStatus "$?" 
  
  ${HOME}/my.scripts/win/video-playlists/create-all-video-playlists-windows.sh
  checkCommandStatus "$?" 
  
  clearMediaServerEhCache

  # create-all-video-playlists-http-niko-server.sh takes about 9mins (2020-10-23)
  ${HOME}/my.scripts/win/video-playlists/create-all-video-playlists-http-niko-server.sh
  checkCommandStatus "$?" 

  ${HOME}/my.scripts/win/video-playlists/create-all-video-playlists-http-niko-server-ip.sh
  checkCommandStatus "$?" 

  ${HOME}/my.scripts/win/video-playlists/create-all-video-playlists-https-kame-server.sh
  checkCommandStatus "$?" 
    
  log.info "Waiting for all background processes to finish in create-all-video-playlists.sh"
  jobs -l
  wait

  pushChangesToGit
}

deleteExistingM3uFiles() {
  log.info "Deleting existing m3u files"
  cd ${PROJECT_DIR}
  local M3U_FILES=`find . | grep -e ".m3u" | sort`;
  while read M3U_FILE; do
    rm -f -v ${M3U_FILE}
  done <<< ${M3U_FILES}
}

checkMediaServer() {
  if [ "${HOSTNAME}" != "${MEDIA_SERVER}" ]; then
    log.error "This script can only run in ${MEDIA_SERVER}. Trying to run in ${HOSTNAME}"
    exitProcess 1
  fi
}

pullChangesFromGit() {
  gitCdCheckoutAndPull "${PROJECT_DIR}" "${GIT_REMOTE}" "${GIT_BRANCH}"
}

pushChangesToGit() {
  gitCdCommitAllChangesAndPush "${PROJECT_DIR}" "${GIT_REMOTE}" "${GIT_BRANCH}" "Updated video playlists"
}

clearMediaServerEhCache() {
  log.info "Clearing ${MEDIA_SERVER} cache"
  curl --location --request DELETE 'localhost:9090/kame-house-media/api/v1/commons/ehcache' \
    --header "Content-Type: application/json" \
    --header "Authorization: Basic ${KH_ADMIN_API_BASIC_AUTH}"
  
}

main "$@"
