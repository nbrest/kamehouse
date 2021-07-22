#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  log.info "Deploying rc-local.sh systemd service"
  sudo chmod 744 /home/nbrest/my.scripts/lin/startup/rc-local.sh
  sudo cp -v /home/nbrest/my.scripts/lin/startup/rc-local.service /etc/systemd/system/rc-local.service 
  sudo chmod 664 /etc/systemd/system/rc-local.service
  sudo systemctl daemon-reload
  sudo systemctl enable rc-local.service
}

main "$@"
