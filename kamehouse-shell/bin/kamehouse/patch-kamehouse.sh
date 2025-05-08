#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  checkValidRootKameHouseProject
  createPatchFile
  sendPatchFile
  applyPatchFile
  removeLocalPatchFile
}

initScriptEnv() {
  PATCH_FILE="kamehouse.patch"
  APPLY_PATCH_ARGS=""
  GIT_PROJECT_DIR="~/git/kamehouse"
  STATIC_ONLY=false
}

createPatchFile() {
  log.info "Creating patch from current directory"
  git reset
  git add *
  git status
  git diff --binary --staged > ${PATCH_FILE}

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
  SCP_SRC="${PATCH_FILE}"
  SCP_DEST="${SSH_USER}@${SSH_SERVER}:${GIT_PROJECT_DIR}/${PATCH_FILE}"
  executeScpCommand
}

applyPatchFile() {
  log.info "Applying patch file in ${COL_PURPLE}${SSH_SERVER}"
  if [ -n "${MODULE_SHORT}" ]; then
    APPLY_PATCH_ARGS="${APPLY_PATCH_ARGS} -m ${MODULE_SHORT}"
  fi 
  if ${STATIC_ONLY}; then
    APPLY_PATCH_ARGS="${APPLY_PATCH_ARGS} -s"
  fi 

  SSH_COMMAND="~/programs/kamehouse-shell/bin/kamehouse/apply-patch-kamehouse.sh ${APPLY_PATCH_ARGS}"
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
