#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Undeploying kamehouse-startup-service.sh systemd service"
  log.warn "User running this script needs ${COL_RED}sudo rm,systemctl${COL_DEFAULT_LOG} permissions"
  sudo rm /etc/systemd/system/kamehouse-startup.service 
  sudo systemctl daemon-reload
  sudo systemctl disable kamehouse-startup.service
}

main "$@"
