#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 149
fi

# Global variables
LOG_PROCESS_TO_FILE=false

main() {  
  # List all torrents folders
  TORRENTS_CSV=$(find ${HOME}/torrents/transmission/downloads -maxdepth 1 -name '.*' -prune -o -type d)

  # Replace \n with :  
  TORRENTS_CSV=$(echo "$TORRENTS_CSV" | tr '\n' ':')
  
  # Remove last :
  TORRENTS_CSV=$(echo "$TORRENTS_CSV" | sed '$s/.$//')

  # Convert : to ,
  TORRENTS_CSV=$(echo "$TORRENTS_CSV" | tr ':' ',')
  
  # Strip base path
  BASE_PATH="${HOME}/torrents/transmission/downloads/"
  TORRENTS_CSV=$(echo "$TORRENTS_CSV" | sed -e "s#${BASE_PATH}##g")
  BASE_PATH="${HOME}/torrents/transmission/downloads"
  TORRENTS_CSV=$(echo "$TORRENTS_CSV" | sed -e "s#${BASE_PATH}##g")
  TORRENTS_CSV=${TORRENTS_CSV:1}
  
  echo ${TORRENTS_CSV}
}

main "$@"
