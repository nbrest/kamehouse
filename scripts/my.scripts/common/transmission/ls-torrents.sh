#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
# LOG_PROCESS_TO_FILE=true
TORRENTS_PATH=""

mainProcess() {
  if ${IS_LINUX_HOST}; then
    TORRENTS_PATH=${HOME}/torrents/transmission/downloads
  else
    TORRENTS_PATH=/d/Downloads/torrents
  fi

  cd ${TORRENTS_PATH}

  pwd

  ls -l ${TORRENTS_PATH}  
}

main "$@"
