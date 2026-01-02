#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  ${HOME}/programs/kamehouse-shell/bin/win/shutdown/shutdown.sh -t 0
}

main "$@"
