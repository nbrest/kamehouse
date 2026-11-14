#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/docker/docker-functions.sh

initScriptEnv() {
  USE_CURRENT_DIR=true
}

mainProcess() {
  checkDockerScripsEnabled
  setKameHouseRootProjectDir
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/docker/docker-build-kamehouse.sh -b
  checkCommandStatus "$?" "Error rebuilding and pushing the kamehouse docker image" 
}

main "$@"
