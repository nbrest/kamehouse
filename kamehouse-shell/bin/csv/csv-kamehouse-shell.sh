#!/bin/bash

# Returns kamehouse-shell scripts as csv as a relative path from kamehouse-shell

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

importKamehouse functions/kamehouse/get-kamehouse-shell-path-content-functions.sh

initKameHouseShellEnv() {
  LOG=DISABLED
}

initScriptEnv() {
  BASE_DIR=${HOME}/programs/kamehouse-shell/bin
}

mainProcess() {  
  local SCRIPTS_PATH=`getKameHouseShellPathContent "${BASE_DIR}" "f"`

  # Convert : to ,
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | tr ':' ',')
  
  # Strip ${BASE_DIR} from the path of each script
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | sed -e "s#${BASE_DIR}/##g")

  echo ","
  echo ${SCRIPTS_PATH}
  echo ","
}

main "$@"
