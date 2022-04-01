#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi
# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

USERHOME_WIN="${HOME}"
USERHOME_LIN="/home/${DEFAULT_KAMEHOUSE_USERNAME}"
HOST=""
HOST_FILE="${HOME}/.kamehouse/host"

main() {
  if ${IS_LINUX_HOST}; then
    if [ -f "${HOST_FILE}" ]; then
      HOST=`cat ${HOST_FILE}`
      if [ "${HOST}" == "aws" ]; then
        USERHOME_LIN="/home/ubuntu"
      fi
    fi
    echo "${USERHOME_LIN}"
  else
    echo "${USERHOME_WIN}"
  fi   
}

main "$@"
