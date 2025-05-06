#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

LOG_PROCESS_TO_FILE=false
DECRYPT_FILE=false

mainProcess() {
  decryptFile
  validateRequiredFiles
  editFile
  keysFolderStatus
  log.info "${COL_RED}********************************************************************"
  log.info "${COL_YELLOW}Encrypt the file with encrypt-kamehouse-secrets.sh when done editing"
  log.info "${COL_RED}********************************************************************"
}

decryptFile() {
  if ${DECRYPT_FILE}; then
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/decrypt-kamehouse-secrets.sh
  fi
}

validateRequiredFiles() {
  if [ ! -f "${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg" ]; then
    log.error "Missing file .kamehouse-secrets.cfg. Run with -d to decrypt secrets for editing"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi
}

editFile() {
  log.info "Editing .kamehouse-secrets.cfg"
  vim ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg
}

keysFolderStatus() {
  log.info "ls -lah ${HOME}/.kamehouse/config/keys"
  ls -lah ${HOME}/.kamehouse/config/keys
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -d)
        DECRYPT_FILE=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-d" "decrypt secrets file before editing"
}

main "$@"
