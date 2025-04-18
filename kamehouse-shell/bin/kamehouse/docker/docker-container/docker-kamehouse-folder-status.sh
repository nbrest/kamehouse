#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  log.info "${HOME}/.kamehouse"
  ls -ltra ${HOME}/.kamehouse

  log.info "${HOME}/.kamehouse/config/keys"
  ls -ltra ${HOME}/.kamehouse/config/keys

  log.info "${HOME}/.kamehouse/config/mariadb"
  ls -ltra ${HOME}/.kamehouse/config/mariadb

  log.info "${HOME}/.kamehouse/config/mariadb/dump"
  ls -ltra ${HOME}/.kamehouse/config/mariadb/dump

  log.info "${HOME}/.ssh"
  ls -ltra ${HOME}/.ssh
}

main "$@"
