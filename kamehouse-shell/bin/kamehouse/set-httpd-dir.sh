#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 99
fi

HTTPD_DIR="${HOME}/programs/apache-httpd"

mainProcess() {
  if ${IS_LINUX_HOST}; then
    HTTPD_DIR="/etc/apache2"
  fi  
  export HTTPD_DIR="${HTTPD_DIR}"
  log.trace "HTTPD_DIR=${HTTPD_DIR}"
}

main "$@"
