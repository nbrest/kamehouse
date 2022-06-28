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
  log.info "Starting apache httpd server"
  if ${IS_LINUX_HOST}; then
    setSudoKameHouseCommand "service apache2 start"
    ${SUDO_KAMEHOUSE_COMMAND}
  else
    export HOME=`${HOME}/programs/kamehouse-shell/bin/kamehouse/get-userhome.sh`
    HTTPD_DIR=`${HOME}/programs/kamehouse-shell/bin/kamehouse/get-httpd-dir.sh`
    cd ${HTTPD_DIR}/bin
    powershell.exe -c "Start-Process ./httpd.exe" &
  fi
}

main "$@"
