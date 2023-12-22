#!/bin/bash
source /var/www/.kamehouse-user

main() {
  COMMAND="/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh $@"

  /usr/bin/su - ${KAMEHOUSE_USER} -c "${COMMAND}"
}

validateCommandLineArguments() {
  if [[ "$@" == *['!'@#\$%^\&*()\<\>\|\;+]* ]]; then
    echo "$(date +%Y-%m-%d' '%H:%M:%S) - [ERROR] - 'su.sh' parameters contain invalid characters. Can't procede to execute script"
    exit 1
  fi
}

validateCommandLineArguments "$@"
main "$@"