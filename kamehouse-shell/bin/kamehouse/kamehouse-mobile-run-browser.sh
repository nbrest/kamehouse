#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

trap cleanupAfterRun INT

initScriptEnv() {
  USE_CURRENT_DIR=true
}

mainProcess() {
  setKameHouseRootProjectDir
  buildKameHouseMobileStatic
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-static-files.sh -c
  cdToKameHouseModule "kamehouse-mobile"
  cordova run browser
  cdToRootDirFromModule "kamehouse-mobile"
}

cleanupAfterRun() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-clean.sh
  ctrlC
}

main "$@"
