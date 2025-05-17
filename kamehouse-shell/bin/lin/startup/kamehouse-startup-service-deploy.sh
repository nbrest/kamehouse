#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Deploying kamehouse-startup-service.sh systemd service"
  log.warn "User running this script needs ${COL_RED}sudo chmod,cp,systemctl${COL_DEFAULT_LOG} permissions"
  local USERNAME=`whoami`
  sudo chmod 744 /home/${USERNAME}/programs/kamehouse-shell/bin/lin/startup/kamehouse-startup-service.sh
  sudo cp -v /home/${USERNAME}/programs/kamehouse-shell/bin/lin/startup/kamehouse-startup.service /etc/systemd/system/kamehouse-startup.service 
  sudo chmod 664 /etc/systemd/system/kamehouse-startup.service
  sudo systemctl daemon-reload
  sudo systemctl enable kamehouse-startup.service
  log.info "/etc/systemd/system/kamehouse-startup.service"
  cat /etc/systemd/system/kamehouse-startup.service
  echo ""
}

main "$@"
