#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

USERNAME="nbrest"
HOST=""
HOST_FILE="${HOME}/home-synced/host"

main() {
  HOSTNAME=`hostname`
  if [ "${HOSTNAME}" == "pi" ]; then
    USERNAME="pi"
  fi

  if [ -f "${HOST_FILE}" ]; then
    HOST=`cat ${HOST_FILE}`
    if [ "${HOST}" == "aws" ]; then
      USERNAME="ubuntu"
    fi
  fi

  echo "${USERNAME}"
  exitProcess 0
}

main "$@"
