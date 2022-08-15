#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=false
MEDIA_SERVER_IP=192.168.0.109

mainProcess() {
  log.info "Call this script with nohup"
  cd ${HOME}/git/noVNC
  ./utils/novnc_proxy --vnc ${MEDIA_SERVER_IP}:5900 --listen 3900 > ${HOME}/logs/novnc.log 2>&1 &
}

main "$@"
