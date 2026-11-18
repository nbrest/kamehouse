#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

importKamehouse functions/git/git-pull-functions.sh

initScriptEnv() {
  GIT_PROJECT_DIR=${HOME}/.kamehouse/git/kamehouse
  GIT_REMOTE=origin
}

mainProcess() {
  log.info "Git pull ${COL_PURPLE}${GIT_BRANCH} ${GIT_PROJECT_DIR}"

  cd ${GIT_PROJECT_DIR}
  checkCommandStatus "$?"

  git reset --hard

  git checkout ${GIT_BRANCH}
  checkCommandStatus "$?"
  
  execWithTimeout ${GIT_TIMEOUT} git pull ${GIT_REMOTE} ${GIT_BRANCH}
  checkCommandStatus "$?"
}

main "$@"
