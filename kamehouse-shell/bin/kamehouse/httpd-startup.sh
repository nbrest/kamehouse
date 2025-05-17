#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

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
