#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
}

mainProcess() {
  validateRequiredFiles
  decryptFile
  editFile
  encryptFile
  keysFolderStatus
}

initScriptEnv() {
  SUFFIX=$RANDOM
}

validateRequiredFiles() {
  if [ ! -f "${HOME}/.kamehouse/config/keys/kamehouse.key" ]; then
    log.error "Missing file kamehouse.key"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi

  if [ ! -f "${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.enc" ]; then
    log.error "Missing file kamehouse-secrets.key.enc"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi

  if [ ! -f "${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc" ]; then
    log.error "Missing file .kamehouse-secrets.cfg.enc"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi
}

decryptFile() {
  log.debug "Decrypting kamehouse-secrets.key.enc"
  openssl pkeyutl -decrypt -inkey ${HOME}/.kamehouse/config/keys/kamehouse.key -in ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.enc -out ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX}
  
  log.info "Decrypting .kamehouse-secrets.cfg.enc with kamehouse-secrets.key.${SUFFIX}"
  openssl enc -d -in ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc -out ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.${SUFFIX} -pbkdf2 -aes256 -kfile ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX}
  local RESULT=$?
  rm -v ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX}

  if [ "${RESULT}" != "0" ]; then
    log.error "Error decrypting .kamehouse-secrets.cfg.enc"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi
}

editFile() {
  log.info "Editing .kamehouse-secrets.cfg.${SUFFIX}"
  vim ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.${SUFFIX}
}

encryptFile() {
  log.debug "Decrypting kamehouse-secrets.key.enc"
  openssl pkeyutl -decrypt -inkey ${HOME}/.kamehouse/config/keys/kamehouse.key -in ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.enc -out ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX}
  
  log.info "Encrypting .kamehouse-secrets.cfg.${SUFFIX} with kamehouse-secrets.key.${SUFFIX}"
  openssl enc -in ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.${SUFFIX} -out ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc -pbkdf2 -aes256 -kfile ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX}
  local RESULT=$?
  rm -v ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX}

  if [ "${RESULT}" != "0" ]; then
    log.error "Error encrypting .kamehouse-secrets.cfg"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi

  log.debug "Deleting decrypted .kamehouse-secrets.cfg.${SUFFIX}"
  rm -v ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.${SUFFIX} 
}

keysFolderStatus() {
  log.info "ls -lah ${HOME}/.kamehouse/config/keys"
  ls -lah ${HOME}/.kamehouse/config/keys
}

main "$@"
