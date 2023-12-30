#!/bin/bash
source /var/www/.kamehouse-user

main() {
  COMMAND="/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh $@"

  /usr/bin/su - ${KAMEHOUSE_USER} -c "${COMMAND}"
}

validateCommandLineArguments() {
  local SUBPATH_REGEX=.*\\.\\.\\/.*
  if [[ "$@" =~ ${SUBPATH_REGEX} ]]; then
    echo "$(date +%Y-%m-%d' '%H:%M:%S) - [ERROR] - 'su.sh' parameters try to escape kamehouse shell base path. Can't procede to execute script"
    exit 3
  fi
  if [[ "$@" == *[\`'!'@#\$%^\&*()\<\>\|\;+]* ]]; then
    echo "$(date +%Y-%m-%d' '%H:%M:%S) - [ERROR] - 'su.sh' parameters contain invalid characters. Can't procede to execute script"
    exit 3
  fi
}

validateCommandLineArguments "$@"
main "$@"