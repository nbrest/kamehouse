#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=true

mainProcess() {
  loadDockerContainerEnv

  if ${IS_DOCKER_CONTAINER}; then
    ${HOME}/programs/kamehouse-shell/bin/lin/git/git-pull-prod-kamehouse.sh
  else
    ${HOME}/programs/kamehouse-shell/bin/lin/git/git-pull-hacking.sh
    ${HOME}/programs/kamehouse-shell/bin/lin/git/git-pull-kamehouse.sh
    ${HOME}/programs/kamehouse-shell/bin/lin/git/git-pull-kamehouse-audio-playlists.sh
    ${HOME}/programs/kamehouse-shell/bin/lin/git/git-pull-kamehouse-video-playlists.sh
    ${HOME}/programs/kamehouse-shell/bin/lin/git/git-pull-kamehouse-webserver.sh
    ${HOME}/programs/kamehouse-shell/bin/lin/git/git-pull-learn-java.sh
    ${HOME}/programs/kamehouse-shell/bin/lin/git/git-pull-my-scripts.sh
    ${HOME}/programs/kamehouse-shell/bin/lin/git/git-pull-programming.sh
    ${HOME}/programs/kamehouse-shell/bin/lin/git/git-pull-prod-kamehouse.sh
    ${HOME}/programs/kamehouse-shell/bin/lin/git/git-pull-texts.sh
  fi
}

main "$@"
