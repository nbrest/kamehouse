#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=false
SERVICE="novnc"

mainProcess() {
  log.info "Call this script with nohup"
  cd ${HOME}/git/noVNC
  copyCerts
  ./utils/novnc_proxy --vnc 192.168.0.109:5900 --listen 3900 --cert ${HOME}/git/noVNC/fullchain.pem --key ${HOME}/git/noVNC/privkey.pem > ${HOME}/logs/novnc.log 2>&1 &
}

copyCerts() {
  sudo cp -L /etc/letsencrypt/live/www.nicobrest.com/fullchain.pem ${HOME}/git/noVNC/fullchain.pem
  sudo chmod a+xr ${HOME}/git/noVNC/fullchain.pem
  sudo cp -L /etc/letsencrypt/live/www.nicobrest.com/privkey.pem ${HOME}/git/noVNC/privkey.pem
  sudo chmod a+xr ${HOME}/git/noVNC/privkey.pem
}

main "$@"
