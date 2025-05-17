#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  setSudoKameHouseCommand "/usr/sbin/reboot"
  ${SUDO_KAMEHOUSE_COMMAND}
}

main "$@"
