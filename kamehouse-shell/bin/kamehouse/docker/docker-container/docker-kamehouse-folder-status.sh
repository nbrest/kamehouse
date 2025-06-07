#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

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
