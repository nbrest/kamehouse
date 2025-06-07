#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  cp -fv ${HOME}/programs/kamehouse-shell/bin/win/bat/b.bat ${HOME}/b.bat
  ls -l ${HOME}/b.bat
}

main "$@"
