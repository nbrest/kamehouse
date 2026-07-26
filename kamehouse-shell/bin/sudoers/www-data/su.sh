#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
  LOG_PROCESS_TO_FILE=false
}

mainProcessLin() {
  validateCommandLineArguments "$@"
  
  source /var/www/.kamehouse-user

  COMMAND="/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/sudoers/www-data/exec-script.sh $@"
  
  if [[ "$@" =~ ^"secrets/get-kamehouse-secret.sh -s ".* ]]; then
    COMMAND="/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/$@"
  fi

  ${COMMAND}
}

validateCommandLineArguments() {
  local SUBPATH_REGEX=.*\\.\\.\\/.*
  if [[ "$@" =~ ${SUBPATH_REGEX} ]]; then
    log.error "'su.sh' parameters try to escape kamehouse shell base path. Can't procede to execute script"
    exit ${EXIT_INVALID_ARG}
  fi
  if [[ "$@" == *[\`'!'@#\$%^\&*()\<\>\|\;+]* ]]; then
    log.error "'su.sh' parameters contain invalid characters. Can't procede to execute script"
    exit ${EXIT_INVALID_ARG}
  fi
}

parseArguments() {
  return
}

main "$@"