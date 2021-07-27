#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=false

NIKO_SERVER_MAC=8C04BAA6F2B1
BROADCAST_IP=192.168.0.255
SUBNET_MASK=255.255.255.0
PORT=9

mainProcess() {
  log.info "Sending WOL packet to niko-server"
  wolcmd ${NIKO_SERVER_MAC} ${BROADCAST_IP} ${SUBNET_MASK} ${PORT}
}

main "$@"
