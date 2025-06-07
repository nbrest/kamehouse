#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  setSudoKameHouseCommand "/usr/sbin/shutdown"
  ${SUDO_KAMEHOUSE_COMMAND} -c
}

main "$@"
