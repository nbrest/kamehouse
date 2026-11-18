#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  ALLOW_PARALLEL_RUNS=true
}

mainProcess() {
  validateRequiredFiles
  decryptFile
  keysFolderStatus
  log.info "${COL_YELLOW}Decryption of .kamehouse-secrets.cfg done successfully!"
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

  if [ ! -f "${HOME}/.kamehouse/secrets/.kamehouse-secrets.cfg.enc" ]; then
    log.error "Missing file .kamehouse-secrets.cfg.enc"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi
}

decryptFile() {
  log.info "Decrypting kamehouse-secrets.key.enc"
  local SUFFIX=$RANDOM
  openssl pkeyutl -decrypt -inkey ${HOME}/.kamehouse/secrets/kamehouse.key -in ${HOME}/.kamehouse/secrets/kamehouse-secrets.key.enc -out ${HOME}/.kamehouse/secrets/kamehouse-secrets.key.${SUFFIX}
  
  log.info "Decrypting .kamehouse-secrets.cfg.enc with kamehouse-secrets.key.${SUFFIX}"
  openssl enc -d -in ${HOME}/.kamehouse/secrets/.kamehouse-secrets.cfg.enc -out ${HOME}/.kamehouse/secrets/.kamehouse-secrets.cfg -pbkdf2 -aes256 -kfile ${HOME}/.kamehouse/secrets/kamehouse-secrets.key.${SUFFIX}
  local RESULT=$?
  rm -v ${HOME}/.kamehouse/secrets/kamehouse-secrets.key.${SUFFIX}

  if [ "${RESULT}" != "0" ]; then
    log.error "Error decrypting .kamehouse-secrets.cfg.enc"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi
}

keysFolderStatus() {
  log.info "ls -lah ${HOME}/.kamehouse/secrets"
  ls -lah ${HOME}/.kamehouse/secrets
}

main "$@"
