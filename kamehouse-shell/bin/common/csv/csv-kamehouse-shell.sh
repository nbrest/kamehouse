#!/bin/bash

# Returns kamehouse-shell scripts as csv as a relative path from kamehouse-shell

# Disable logs
LOG=ERROR
SKIP_LOG_START_FINISH=true

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

BASE_DIR=${HOME}/programs/kamehouse-shell/bin

mainProcess() {  
  # List all files
  SCRIPTS_PATH=$(find ${BASE_DIR} -name '*')

  # Filter bashrc
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /common/bashrc) 
  # Filter docker container scripts
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /kamehouse/docker/docker-container)
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /kamehouse/docker/release/java8-release/bin)
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /kamehouse/docker/release/java8-release/docker)
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /kamehouse/docker/release/java11-release/bin)
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /kamehouse/docker/release/java11-release/docker)    
  # Filter win/lin paths
  if ${IS_LINUX_HOST}; then
    SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v '/win/') 
  else
    SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v '/lin/') 
  fi
  
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
