#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
	echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/git/git-pull-functions.sh
if [ "$?" != "0" ]; then
	echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing git-pull-functions.sh" ; exit 99
fi

initScriptEnv() {
  GIT_PROJECT_DIR=${HOME}/git/kamehouse
  GIT_REMOTE=origin
}

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

main "$@"
