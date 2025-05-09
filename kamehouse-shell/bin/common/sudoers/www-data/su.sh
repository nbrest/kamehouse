#!/bin/bash
source /var/www/.kamehouse-user

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  COMMAND="/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh $@"
  
  if [[ "$@" =~ ^"kamehouse/get-kamehouse-secret.sh -s ".* ]]; then
    COMMAND="/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/$@"
  fi

  /usr/bin/su - ${KAMEHOUSE_USER} -c "${COMMAND}"
}

validateCommandLineArguments() {
  local SUBPATH_REGEX=.*\\.\\.\\/.*
  if [[ "$@" =~ ${SUBPATH_REGEX} ]]; then
    echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - 'su.sh' parameters try to escape kamehouse shell base path. Can't procede to execute script"
    exit ${EXIT_INVALID_ARG}
  fi
  if [[ "$@" == *[\`'!'@#\$%^\&*()\<\>\|\;+]* ]]; then
    echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - 'su.sh' parameters contain invalid characters. Can't procede to execute script"
    exit ${EXIT_INVALID_ARG}
  fi
}

validateCommandLineArguments "$@"
main "$@"