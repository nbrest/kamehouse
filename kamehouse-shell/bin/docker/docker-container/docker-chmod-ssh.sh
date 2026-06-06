#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  chmod 755 ${HOME}
  chmod 700 ${HOME}/.ssh 
  chmod 644 ${HOME}/.ssh/authorized_keys
  chmod 600 ${HOME}/.ssh/config
  chmod 600 ${HOME}/.ssh/id_* 
  chmod 644 ${HOME}/.ssh/*.pub
  chmod 644 ${HOME}/.ssh/known_hosts
}

main "$@"
