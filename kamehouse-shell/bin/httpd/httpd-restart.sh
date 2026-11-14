#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Restarting apache httpd server"

  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/httpd/httpd-stop.sh
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/httpd/httpd-startup.sh
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/httpd/httpd-status.sh
}

main "$@"
