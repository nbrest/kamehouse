#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

trap cleanupAfterRun INT

initScriptEnv() {
  USE_CURRENT_DIR=true
}

mainProcess() {
  setKameHouseRootProjectDir
  buildKameHouseMobileStatic
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/mobile/kamehouse-mobile-resync-static-files.sh -c
  cdToKameHouseModule "kamehouse-mobile"
  cordova run browser
  cdToRootDirFromModule "kamehouse-mobile"
}

cleanupAfterRun() {
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/mobile/kamehouse-mobile-clean.sh
  ctrlC
}

main "$@"
