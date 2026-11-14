#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "${HOME}/.kamehouse"
  ls -ltra ${HOME}/.kamehouse

  log.info "${HOME}/.kamehouse/secrets"
  ls -ltra ${HOME}/.kamehouse/secrets

  log.info "${HOME}/.kamehouse/backup/db"
  ls -ltra ${HOME}/.kamehouse/backup/db

  log.info "${HOME}/.kamehouse/backup/db/dump"
  ls -ltra ${HOME}/.kamehouse/backup/db/dump

  log.info "${HOME}/.ssh"
  ls -ltra ${HOME}/.ssh
}

main "$@"
