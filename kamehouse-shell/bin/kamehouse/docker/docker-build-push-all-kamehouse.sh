#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then echo "Error importing docker-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  USE_CURRENT_DIR=true
}

mainProcess() {
  checkDockerScripsEnabled
  setKameHouseRootProjectDir
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-build-kamehouse.sh -b
  checkCommandStatus "$?" "Error rebuilding and pushing the kamehouse docker image" 
}

main "$@"
