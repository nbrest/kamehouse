#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcessLin() {
  sudo poweroff
}

mainProcessWin() {
  ${HOME}/programs/kamehouse-shell/bin/shutdown/shutdown.sh -d 0
}

main "$@"
