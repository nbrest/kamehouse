#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

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

  if [ ! -f "./docker/keys/.kamehouse-secrets.cfg" ]; then
    log.error "Missing sample file .kamehouse-secrets.cfg"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi
}

encryptFile() {
  log.info "Encrypting sample .kamehouse-secrets.cfg"
  openssl enc -in ./docker/keys/.kamehouse-secrets.cfg -out ./docker/keys/.kamehouse-secrets.cfg.enc -pbkdf2 -aes256 -kfile ./kamehouse-commons-core/src/test/resources/commons/keys/secrets.key
  local RESULT=$?
  if [ "${RESULT}" != "0" ]; then
    log.error "Error encrypting sample .kamehouse-secrets.cfg"
    keysFolderStatus
    exitProcess ${EXIT_ERROR}
  fi

  log.info "${COL_YELLOW}Encryption of sample .kamehouse-secrets.cfg done successfully!"
}

keysFolderStatus() {
  log.info "ls -lah ./docker/keys/"
  ls -lah ./docker/keys/
}

main "$@"
