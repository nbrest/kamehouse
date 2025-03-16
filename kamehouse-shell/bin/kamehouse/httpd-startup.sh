#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  log.info "Starting apache httpd server"
  if ${IS_LINUX_HOST}; then
    setSudoKameHouseCommand "/usr/sbin/service apache2 start"
    ${SUDO_KAMEHOUSE_COMMAND}
  else
    source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-userhome.sh
    source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-httpd-dir.sh
    log.info "Starting httpd from ${HTTPD_DIR}"
    cd ${HTTPD_DIR}/bin
    powershell.exe -c "Start-Process -WindowStyle Minimized ./httpd.exe" &
  fi
}

main "$@"
