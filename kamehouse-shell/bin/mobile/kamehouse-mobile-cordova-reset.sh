#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  USE_CURRENT_DIR=true
}

mainProcess() {
  setKameHouseRootProjectDir
  cdToKameHouseModule "kamehouse-mobile"
  resetCordovaProject
  cdToRootDirFromModule "kamehouse-mobile"
}

resetCordovaProject() {
  log.info "Removing generated cordova platforms, plugins and node modules from ${COL_PURPLE}$(pwd)"
  rm -rf platforms plugins node_modules
}

main "$@"
