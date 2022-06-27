#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  log.info "Deploying rc-local.sh systemd service"
  log.warn "User running this script needs ${COL_RED}sudo chmod,cp,systemctl${COL_DEFAULT_LOG} permissions"
  local USERNAME=`whoami`
  sudo chmod 744 /home/${USERNAME}/programs/kamehouse-shell/bin/lin/startup/rc-local.sh
  sudo cp -v /home/${USERNAME}/programs/kamehouse-shell/bin/lin/startup/rc-local.service /etc/systemd/system/rc-local.service 
  sudo chmod 664 /etc/systemd/system/rc-local.service
  sudo systemctl daemon-reload
  sudo systemctl enable rc-local.service
}

main "$@"
