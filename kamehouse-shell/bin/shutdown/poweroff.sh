#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  if ${IS_LINUX_HOST}; then
    processLin
  else
    processWin
  fi
}

processLin() {
  sudo poweroff
}

processWin() {
  ${HOME}/programs/kamehouse-shell/bin/shutdown/shutdown.sh -d 0
}

main "$@"
