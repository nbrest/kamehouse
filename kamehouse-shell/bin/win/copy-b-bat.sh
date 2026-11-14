#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcessWin() {
  cp -fv ${HOME}/.kamehouse/opt/kamehouse-shell/bin/win/bat/b.bat ${HOME}/b.bat
  ls -l ${HOME}/b.bat
}

main "$@"
