#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/programs/kamehouse-shell/bin/common/audio-playlists/audio-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing audio-playlists-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  checkAudioServer
  pullChangesFromGit
  createWindowsPlaylists

  ${HOME}/programs/kamehouse-shell/bin/win/audio-playlists/create-all-audio-playlists-linux.sh
  checkCommandStatus "$?" 
  
  ${HOME}/programs/kamehouse-shell/bin/win/audio-playlists/create-all-audio-playlists-android-internal.sh
  checkCommandStatus "$?"

  ${HOME}/programs/kamehouse-shell/bin/win/audio-playlists/create-all-audio-playlists-android-external.sh
  checkCommandStatus "$?"

  log.info "Waiting for all background processes to finish in create-all-audio-playlists.sh"
  jobs -l
  wait

  pushChangesToGit
}

createWindowsPlaylists() {
  rm -rf ${PROJECT_DIR}/windows
  mkdir -p ${PROJECT_DIR}/windows
  ${HOME}/programs/kamehouse-shell/bin/win/audio-playlists/create-all-audio-playlists-windows.sh
  CREATE_WIN_PLS_RESULT="$?"
  if [ "${CREATE_WIN_PLS_RESULT}" != "0" ]; then
    log.error "Error creating windows playlists. Can't proceed to create the rest"
    exitProcess 1
  fi
}

main "$@"
