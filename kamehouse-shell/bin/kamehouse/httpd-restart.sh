#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Restarting apache httpd server"

  if ${IS_LINUX_HOST}; then
    ${HOME}/programs/kamehouse-shell/bin/lin/kamehouse/httpd-stop.sh
  else
    ${HOME}/programs/kamehouse-shell/bin/win/kamehouse/httpd-stop.sh
  fi
  
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/httpd-startup.sh

  if ${IS_LINUX_HOST}; then
    ${HOME}/programs/kamehouse-shell/bin/lin/kamehouse/httpd-status.sh
  else
    ${HOME}/programs/kamehouse-shell/bin/win/kamehouse/httpd-status.sh
  fi
}

main "$@"
