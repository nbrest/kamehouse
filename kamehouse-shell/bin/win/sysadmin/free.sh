#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
# LOG_PROCESS_TO_FILE=true

mainProcess() {
  log.info "Executing 'powershell.exe -c \"systeminfo\" | grep Memory'"
  powershell.exe -c "systeminfo" | grep Memory
}

main "$@"
