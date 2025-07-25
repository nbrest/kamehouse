#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  validateRequiredFiles
  encryptFile
  keysFolderStatus
}

validateRequiredFiles() {
  if [ ! -f "./kamehouse-commons-core/src/test/resources/commons/keys/secrets.key" ]; then
    log.error "Missing sample file secrets.key"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi

  if [ ! -f "./docker/setup-kamehouse/keys/.kamehouse-secrets.cfg" ]; then
    log.error "Missing sample file .kamehouse-secrets.cfg"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi
}

encryptFile() {
  log.info "Encrypting sample .kamehouse-secrets.cfg"
  openssl enc -in ./docker/setup-kamehouse/keys/.kamehouse-secrets.cfg -out ./docker/setup-kamehouse/keys/.kamehouse-secrets.cfg.enc -pbkdf2 -aes256 -kfile ./kamehouse-commons-core/src/test/resources/commons/keys/secrets.key
  local RESULT=$?
  if [ "${RESULT}" != "0" ]; then
    log.error "Error encrypting sample .kamehouse-secrets.cfg"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi

  log.info "${COL_YELLOW}Encryption of sample .kamehouse-secrets.cfg done successfully!"
}

keysFolderStatus() {
  log.info "ls -lah ./docker/setup-kamehouse/keys/"
  ls -lah ./docker/setup-kamehouse/keys/
}

main "$@"
