#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
# LOG_PROCESS_TO_FILE=true
GLOBAL_VAR="Use this script as a base for new scripts"

mainProcess() {
  sudo find /var/lib/jenkins -type f -name '*.war' -exec rm -v {} +
}

main "$@"
