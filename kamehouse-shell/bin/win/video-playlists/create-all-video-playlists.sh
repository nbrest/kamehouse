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
source ${HOME}/.kamehouse/.shell/.cred

GIT_REMOTE=all
GIT_BRANCH=dev

mainProcess() {
  checkMediaServer
  pullChangesFromGit
  removeSpecialCharsInAllFilenames
  deleteExistingM3uFiles

  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/create-base-video-playlists-windows-bash.sh
  checkCommandStatus "$?" 
  
  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/create-mix-video-playlists-windows-bash.sh
  checkCommandStatus "$?" 
  
  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/create-all-video-playlists-linux.sh
  checkCommandStatus "$?" 
  
  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/create-all-video-playlists-windows.sh
  checkCommandStatus "$?" 
  
  clearMediaServerEhCache

  # create-all-video-playlists-http-media-server.sh takes about 9mins (2020-10-23)
  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/create-all-video-playlists-http-media-server.sh
  checkCommandStatus "$?" 

  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/create-all-video-playlists-http-media-server-ip.sh
  checkCommandStatus "$?" 
  
  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/create-all-video-playlists-http-media-server-vlc.sh
  checkCommandStatus "$?" 

  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/create-all-video-playlists-sftp-media-server-vlc.sh
  checkCommandStatus "$?" 

  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/create-all-video-playlists-smb-media-server-vlc.sh
  checkCommandStatus "$?" 

  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/create-all-video-playlists-lan-media-server-vlc.sh
  checkCommandStatus "$?" 

  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/create-all-video-playlists-https-kame-server.sh
  checkCommandStatus "$?" 

  log.info "Waiting for all background processes to finish in create-all-video-playlists.sh"
  jobs -l
  wait

  pushChangesToGit

  ${HOME}/programs/kamehouse-shell/bin/win/video-playlists/resync-subtitles.sh
  checkCommandStatus "$?" 
}

removeSpecialCharsInAllFilenames() {
  removeSpecialCharsInFilenames "/n/anime"
  removeSpecialCharsInFilenames "/n/cartoons"
  removeSpecialCharsInFilenames "/n/funny_videos"
  removeSpecialCharsInFilenames "/n/futbol"
  removeSpecialCharsInFilenames "/n/futbol_4K"
  removeSpecialCharsInFilenames "/n/movies"
  removeSpecialCharsInFilenames "/n/music_videos"
  removeSpecialCharsInFilenames "/n/series"
  removeSpecialCharsInFilenames "/n/tennis"
  removeSpecialCharsInFilenames "/n/Videos-Mobile"
  removeSpecialCharsInFilenames "/n/series"
  removeSpecialCharsInFilenames "/n/series"
}

removeSpecialCharsInFilenames() {
  local FILES_BASE_PATH=$1
  log.info "Removing special chars from filenames in ${COL_PURPLE}${FILES_BASE_PATH}"
  find ${FILES_BASE_PATH} | sort -r | while read FILE; do
    FILE_UPDATED=$(echo "$FILE" | sed '$s'"/${SPECIAL_CHARS_REGEX}/-/g")
    log.trace "Processing file ${COL_PURPLE}${FILE}"
    if [ "${FILE}" != "${FILE_UPDATED}" ]; then
      log.info "Updating name from ${COL_PURPLE}${FILE}${COL_DEFAULT_LOG} to ${COL_CYAN}${FILE_UPDATED}${COL_DEFAULT_LOG}"
      mv "${FILE}" "${FILE_UPDATED}"
    fi
  done
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
  log.debug "curl url: localhost:9090/kame-house-media/api/v1/commons/ehcache"
  curl --location --request DELETE 'localhost:9090/kame-house-media/api/v1/commons/ehcache' \
    --header "Content-Type: application/json" \
    --header "Authorization: Basic ${KH_ADMIN_API_BASIC_AUTH}"
    
}

main "$@"
