#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

PATCH_FILE="kamehouse.patch"
STATIC_ONLY=false
DEPLOYMENT_COMMAND="deploy-kamehouse.sh -c "
GIT_PROJECT_DIR="~/git/kamehouse"

mainProcess() {
  checkValidRootKameHouseProject
  buildDeploymentCommand
  createPatchFile
  sendPatchFile
  applyPatchFile
  removeLocalPatchFile
}

buildDeploymentCommand() {
  if [ -n "${MODULE_SHORT}" ]; then
    DEPLOYMENT_COMMAND="${DEPLOYMENT_COMMAND} -m ${MODULE_SHORT}"
  fi 
  if ${STATIC_ONLY}; then
    DEPLOYMENT_COMMAND="${DEPLOYMENT_COMMAND} -s"
  fi 
}

createPatchFile() {
  log.info "Creating patch from current directory"
  git add *
  git status
  git diff --staged > ${PATCH_FILE}

  log.info "Patch file status"
  ls -lh ${PATCH_FILE}

  if [ ! -s ${PATCH_FILE} ]; then
    log.error "${PATCH_FILE} is empty. Are there any changes to patch?"
    rm ${PATCH_FILE}
    exitProcess ${EXIT_ERROR}
  fi    
}

sendPatchFile() {
  log.info "Sending patch file to ${COL_PURPLE}${SSH_SERVER}"
  scp -v  ${PATCH_FILE} ${SSH_USER}@${SSH_SERVER}:${GIT_PROJECT_DIR}/${PATCH_FILE}
}

applyPatchFile() {
  log.info "Applying patch file in ${COL_PURPLE}${SSH_SERVER}"
  SSH_COMMAND="cd ${GIT_PROJECT_DIR} ; ls -lh ${PATCH_FILE} ; git reset --hard ; git pull origin dev ; git apply ${PATCH_FILE} ; git status ; ${DEPLOYMENT_COMMAND} ; git clean -d -x -f ; git reset --hard ; git status"
  executeSshCommand  
}

removeLocalPatchFile() {
  log.info "Deleting local patch file"
  rm ${PATCH_FILE}
}

parseArguments() {
  parseKameHouseModule "$@"
  parseKameHouseServer "$@"
  
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -z|-m)
        # parsed in a previous parse options function 
        ;;
      -s)
        STATIC_ONLY=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done      
}

setEnvFromArguments() {
  setEnvForKameHouseModule
  setEnvForKameHouseServer
}

printHelpOptions() {
  printKameHouseModuleOption "deploy"
  printKameHouseServerOption
  addHelpOption "-s" "deploy static ui code only"
}

main "$@"
