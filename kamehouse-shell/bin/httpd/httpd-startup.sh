#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcessPre() {
  log.info "Starting apache httpd server"
}

mainProcessLin() {
  setSudoKameHouseCommand "/usr/sbin/service apache2 start"
  ${SUDO_KAMEHOUSE_COMMAND}
}

mainProcessWin() {
  source ${HOME}/programs/kamehouse-shell/bin/deploy/set-userhome.sh
  source ${HOME}/programs/kamehouse-shell/bin/deploy/set-httpd-dir.sh
  log.info "Starting httpd from ${HTTPD_DIR}"
  cd ${HTTPD_DIR}/bin
  powershell.exe -c "Start-Process -WindowStyle Minimized ./httpd.exe" &
}

main "$@"
