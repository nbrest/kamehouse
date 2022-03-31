#!/bin/bash

# Returns kamehouse-shell scripts as csv as a relative path from kamehouse-shell

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=false
BASE_DIR=${HOME}/programs/kamehouse-shell/bin

main() {  
  # List all files
  SCRIPTS_PATH=$(find ${BASE_DIR} -name '.*' -prune -o -type f)

  ##########################################################################
  # copied from lin/bashrc/path.sh
  ##########################################################################
  # Filter aws
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v '/aws/')
  # Filter bashrc
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /aws/bashrc) 
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /lin/bashrc) 
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /win/bashrc) 
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /common/bashrc) 
  # Filter deprecated
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /deprecated)
  # Filter sudoers
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v /lin/sudoers)
  # Filter win
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v '/win/') 
  # Filter .. directory
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep -v '/../')
  ##########################################################################
  
  # Keep only .sh scripts
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | grep '.sh') 

  ##########################################################################
  # copied from lin/bashrc/path.sh
  ##########################################################################
  # Replace \n with :  
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | tr '\n' ':')
  # Remove last :
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | sed '$s/.$//')
  ##########################################################################

  # Convert : to ,
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | tr ':' ',')
  
  # Strip ${BASE_DIR} from the path of each script
  SCRIPTS_PATH=$(echo "$SCRIPTS_PATH" | sed -e "s#${BASE_DIR}/##g")

  echo ${SCRIPTS_PATH}
}

main "$@"
