#!/bin/bash

if (( $EUID == 0 )); then
  HOME="/var/www"
fi

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi
# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=true

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