#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=true
HTPASSWD_CMD=${HOME}/programs/apache-httpd/bin/htpasswd.exe
HTPASSWD_FILE=${HOME}/programs/apache-httpd/www/kh.webserver/.htpasswd

mainProcess() {
  local USERNAME=$1
  local PASSWORD=$2
  
  if ${IS_LINUX_HOST}; then
    HTPASSWD_CMD=htpasswd
    HTPASSWD_FILE=/var/www/kh.webserver/.htpasswd
  fi

  ${HTPASSWD_CMD} -vb ${HTPASSWD_FILE} ${USERNAME} ${PASSWORD}
  RESULT=$?
  if [ "${RESULT}" != "0" ]; then
    echo "loginStatus=ERROR"
  else
    echo "loginStatus=SUCCESS"
  fi
}

main "$@"
