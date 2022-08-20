#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=true
SERVICE="expressvpn"
SERVICE_STARTUP="/usr/bin/expressvpn connect"
EXPRESSVPN_STATUS=

mainProcess() {
  EXPRESSVPN_STATUS=`expressvpn status`
  echo -e "${EXPRESSVPN_STATUS}"

  if [[ "${EXPRESSVPN_STATUS}" =~ "Connecting..." ]]; then
    log.info "Expressvpn status is Connecting. Disconnect and connect again"
    expressvpn disconnect
  fi

  if [[ "${EXPRESSVPN_STATUS}" =~ "Reconnecting..." ]]; then
    log.info "Expressvpn status is Reconnecting. Disconnect and connect again"
    expressvpn disconnect
  fi

  if [[ "${EXPRESSVPN_STATUS}" =~ "Unable" ]]; then
    log.info "Expressvpn status is Unable to connect. Disconnect and connect again"
    expressvpn disconnect
  fi

  log.info "Attempting to connect to expressvpn"
  ${SERVICE_STARTUP} &
}

main "$@"
