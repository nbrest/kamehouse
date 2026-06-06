#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Restarting apache httpd server"

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/httpd/httpd-stop.sh
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/httpd/httpd-startup.sh
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/httpd/httpd-status.sh
}

main "$@"
