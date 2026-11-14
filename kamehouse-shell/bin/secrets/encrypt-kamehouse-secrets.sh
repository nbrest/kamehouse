#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  validateRequiredFiles
  encryptFile
  keysFolderStatus
  log.info "${COL_YELLOW}Encryption of .kamehouse-secrets.cfg done successfully!"
}

validateRequiredFiles() {
  if [ ! -f "${HOME}/.kamehouse/secrets/kamehouse.key" ]; then
    log.error "Missing file kamehouse.key"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi

  if [ ! -f "${HOME}/.kamehouse/secrets/kamehouse-secrets.key.enc" ]; then
    log.error "Missing file kamehouse-secrets.key.enc"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi

  if [ ! -f "${HOME}/.kamehouse/secrets/.kamehouse-secrets.cfg" ]; then
    log.error "Missing file .kamehouse-secrets.cfg"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi
}

encryptFile() {
  log.info "Decrypting kamehouse-secrets.key.enc"
  local SUFFIX=$RANDOM
  openssl pkeyutl -decrypt -inkey ${HOME}/.kamehouse/secrets/kamehouse.key -in ${HOME}/.kamehouse/secrets/kamehouse-secrets.key.enc -out ${HOME}/.kamehouse/secrets/kamehouse-secrets.key.${SUFFIX}
  
  log.info "Encrypting .kamehouse-secrets.cfg with kamehouse-secrets.key.${SUFFIX}"
  openssl enc -in ${HOME}/.kamehouse/secrets/.kamehouse-secrets.cfg -out ${HOME}/.kamehouse/secrets/.kamehouse-secrets.cfg.enc -pbkdf2 -aes256 -kfile ${HOME}/.kamehouse/secrets/kamehouse-secrets.key.${SUFFIX}
  local RESULT=$?
  rm -v ${HOME}/.kamehouse/secrets/kamehouse-secrets.key.${SUFFIX}

  if [ "${RESULT}" != "0" ]; then
    log.error "Error encrypting .kamehouse-secrets.cfg"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi

  log.info "Deleting decrypted .kamehouse-secrets.cfg"
  rm -v ${HOME}/.kamehouse/secrets/.kamehouse-secrets.cfg 
}

keysFolderStatus() {
  log.info "ls -lah ${HOME}/.kamehouse/secrets"
  ls -lah ${HOME}/.kamehouse/secrets
}

main "$@"
