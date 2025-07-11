#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  PATCH_FILE="kamehouse.patch"
  DEPLOYMENT_COMMAND="${HOME}/programs/kamehouse-shell/bin/kamehouse/deploy/deploy-kamehouse.sh -c "
  GIT_PROJECT_DIR="${HOME}/git/kamehouse"
}

mainProcess() {
  applyPatchFile
  runDeployment
  resetGitDir
}

applyPatchFile() {
  log.info "Applying patch file"
  cd "${GIT_PROJECT_DIR}"
  log.info "ls -lh ${PATCH_FILE}"
  ls -lh ${PATCH_FILE} 
  log.info "git reset --hard "
  git reset --hard 
  log.info "git pull origin dev"
  git pull origin dev  
  log.info "git apply ${PATCH_FILE}"
  git apply ${PATCH_FILE}
  if [ "$?" != "0" ]; then
    log.error "Error applying patch file. Can't continue"
    exitProcess ${EXIT_ERROR}
  fi  
  log.info "git status"
  git status
}

runDeployment() {
  if [ -n "${MODULE_SHORT}" ]; then
    DEPLOYMENT_COMMAND="${DEPLOYMENT_COMMAND} -m ${MODULE_SHORT}"
  fi

  ${DEPLOYMENT_COMMAND}
  if [ "$?" == "0" ]; then
    log.info "${COL_YELLOW}SUCCESS!!!!!! Patch applied successfully"
  else
    log.error "Deployment error after applying patch"
  fi
}

resetGitDir() {
  log.info "Resetting ${GIT_PROJECT_DIR}"
  log.info "git clean -d -x -f "
  git clean -d -x -f 
  log.info "git reset --hard"
  git reset --hard 
  log.info "git status"
  git status
}

parseArguments() {
  parseKameHouseModule "$@"
  
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -m)
        # parsed in a previous parse options function 
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done      
}

setEnvFromArguments() {
  setEnvForKameHouseModule
}

printHelpOptions() {
  printKameHouseModuleOption "deploy"
}

main "$@"
