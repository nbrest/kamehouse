#!/bin/bash

# Returns my.scripts as csv as a relative path from my.scripts repo

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=false

main() {  
  # List all files in my.scripts
  MY_SCRIPTS_PATH=$(find ${HOME}/my.scripts -name '.*' -prune -o -type f)
  
  # Filter bashrc
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v aws/bashrc) 
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v lin/bashrc) 
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v win/bashrc) 
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v common/bashrc) 
  # Filter deprecated
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v deprecated)
  # Filter sudoers
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v lin/sudoers)
  # Filter lin
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v 'my.scripts/lin') 
  # Filter pi
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v 'my.scripts/pi') 
  # Filter aws
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v 'my.scripts/aws') 
  # Filter .. directory
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v 'my.scripts/\..*') 
  
  # Keep only .sh scripts
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep '.sh') 
 
  # Replace \n with :  
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | tr '\n' ':')
  # Remove last :
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | sed '$s/.$//') 

  # Convert : to ,
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | tr ':' ',')
  
  # Strip ${HOME}/my.scripts/ from the path of each script
  MY_HOME="${HOME}/my.scripts/"
  MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | sed -e "s#${MY_HOME}##g")

  echo ${MY_SCRIPTS_PATH}
}

main "$@"
