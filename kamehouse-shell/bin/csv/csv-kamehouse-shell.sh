#!/bin/bash

# Returns kamehouse-shell scripts as csv as a relative path from kamehouse-shell

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

initScriptEnv() {
  BASE_DIR=${HOME}/programs/kamehouse-shell/bin
}

mainProcess() {  
  # List all files
  SCRIPTS_PATH=$(find ${BASE_DIR} -name '.*' -prune -o -type f)

  # Filter bashrc
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /bashrc) 
  # Filter docker container scripts
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /docker/docker-container)
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /docker/release/java8-release/bin)
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /docker/release/java8-release/docker)
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /docker/release/java11-release/bin)
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /docker/release/java11-release/docker)    
  
  # Filter -functions.sh scripts
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v -e '\-functions\.sh$') 

  # Keep only .sh scripts
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -e '\.sh$') 

  # Replace \n with :  
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | tr '\n' ':')

  # Remove last :
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | sed '$s/.$//')

  # Convert : to ,
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | tr ':' ',')
  
  # Strip ${BASE_DIR} from the path of each script
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | sed -e "s#${BASE_DIR}/##g")

  echo ","
  echo ${SCRIPTS_PATH}
  echo ","
}

main "$@"
