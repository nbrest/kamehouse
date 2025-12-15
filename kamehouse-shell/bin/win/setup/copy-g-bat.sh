#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  cp -fv ${HOME}/programs/kamehouse-shell/bin/win/bat/g.bat ${HOME}/g.bat
  ls -l ${HOME}/m.bat
}

main "$@"
