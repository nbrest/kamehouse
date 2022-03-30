#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

LOG_PROCESS_TO_FILE=false
DEFAULT_VLC_PORT="8080"
VLC_PORT=${DEFAULT_VLC_PORT}

mainProcess() {
  netstat -ano | grep "LISTENING" | grep "\[::\]:${VLC_PORT} " | tail -n 1
}

main "$@"
