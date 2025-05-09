#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

mainProcess() {
  validateRequiredFiles
  decryptFile
  keysFolderStatus
  log.info "${COL_YELLOW}Decryption of .kamehouse-secrets.cfg done successfully!"
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
  log.info "Decrypting kamehouse-secrets.key.enc"
  local SUFFIX=$RANDOM
  openssl pkeyutl -decrypt -inkey ${HOME}/.kamehouse/config/keys/kamehouse.key -in ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.enc -out ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX}
  
  log.info "Decrypting .kamehouse-secrets.cfg.enc with kamehouse-secrets.key.${SUFFIX}"
  openssl enc -d -in ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc -out ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg -pbkdf2 -aes256 -kfile ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX}
  local RESULT=$?
  rm -v ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX}

  if [ "${RESULT}" != "0" ]; then
    log.error "Error decrypting .kamehouse-secrets.cfg.enc"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi
}

keysFolderStatus() {
  log.info "ls -lah ${HOME}/.kamehouse/config/keys"
  ls -lah ${HOME}/.kamehouse/config/keys
}

main "$@"
