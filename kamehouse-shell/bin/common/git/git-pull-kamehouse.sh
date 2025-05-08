#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/git/git-pull-functions.sh
if [ "$?" != "0" ]; then
	echo "$(date +%Y-%m-%d' '%H:%M:%S) - [ERROR] - An error occurred importing git-pull-functions.sh"
	exit 99
fi

mainProcess() {
  log.info "Git pull ${COL_PURPLE}${GIT_BRANCH} ${GIT_PROJECT_DIR}"

  cd ${GIT_PROJECT_DIR}
  checkCommandStatus "$?"

  git reset --hard

  git checkout ${GIT_BRANCH}
  checkCommandStatus "$?"
  
  git pull ${GIT_REMOTE} ${GIT_BRANCH}
  checkCommandStatus "$?"
}

initScriptEnv() {
  GIT_PROJECT_DIR=${HOME}/git/kamehouse
  GIT_REMOTE=origin
}

main "$@"
