#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcessLin() {
  log.info "kamehouse-startup.service status"
  sudo service kamehouse-startup status
}

main "$@"
