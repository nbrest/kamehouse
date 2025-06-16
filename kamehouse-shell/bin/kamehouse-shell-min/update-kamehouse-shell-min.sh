#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  SRC_PROJECT_DIR="${HOME}/git/kamehouse"
  PROJECT_DIR="${HOME}/git/kamehouse-shell-min"
  GIT_COMMIT_HASH=""
}

mainProcess() {
  log.info "Updating kamehouse-shell-min git repo"
  pullChangesFromGit
  updateKameHouseShellMin
  updateCommonFunctionsImport
  updateCommitVersionInReadme
  pushChangesToGit
}

pullChangesFromGit() {
  gitCdCheckoutAndPull "${SRC_PROJECT_DIR}" "all" "dev"
  setGitCommitHash
}

updateKameHouseShellMin() {
  log.info "Updating kamehouse-shell-min source files from ${COL_PURPLE}${SRC_PROJECT_DIR}"
  rm -rf ${PROJECT_DIR}/bin
  mkdir -p ${PROJECT_DIR}/bin/common/functions
  cp -vf ${SRC_PROJECT_DIR}/kamehouse-shell/bin/common/functions/colors-functions.sh ${PROJECT_DIR}/bin/common/functions/
  cp -vf ${SRC_PROJECT_DIR}/kamehouse-shell/bin/common/functions/common-functions.sh ${PROJECT_DIR}/bin/common/functions/
  cp -vf ${SRC_PROJECT_DIR}/kamehouse-shell/bin/common/functions/default-functions.sh ${PROJECT_DIR}/bin/common/functions/
  cp -vf ${SRC_PROJECT_DIR}/kamehouse-shell/bin/common/functions/log-functions.sh ${PROJECT_DIR}/bin/common/functions/
  cp -vf ${SRC_PROJECT_DIR}/kamehouse-shell/bin/common/functions/minimal-functions.sh ${PROJECT_DIR}/bin/common/functions/
}

updateCommonFunctionsImport() {
  log.info "Removing unnecessary imports from common-functions.sh"
  cd ${PROJECT_DIR}/bin/common/functions/
  sed -i "s# \"extended-functions.sh\" \"git/git-functions.sh\"##g" common-functions.sh
}

updateCommitVersionInReadme() {
  log.info "Updating readme with git commit hash ${COL_PURPLE}${GIT_COMMIT_HASH}"
  cd ${PROJECT_DIR}
  sed -i -E "s#Using kamehouse's commit version \[.*\]\(https://github\.com/nbrest/kamehouse/tree/.*\)#Using kamehouse's commit version \[${GIT_COMMIT_HASH}\]\(https://github\.com/nbrest/kamehouse/tree/${GIT_COMMIT_HASH}\)#g" README.md
}

pushChangesToGit() {
  gitCdCommitAllChangesAndPush "${PROJECT_DIR}" "origin" "master" "Updated kamehouse-shell-min to use kamehouse version ${GIT_COMMIT_HASH}"
}

main "$@"
