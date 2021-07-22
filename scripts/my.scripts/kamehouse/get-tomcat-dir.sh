#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

TOMCAT_DIR="${HOME}/programs/apache-tomcat"

main() {
  if ${IS_LINUX_HOST}; then
      if [ -d "/var/lib/tomcat7" ]; then
        TOMCAT_DIR="/var/lib/tomcat7"
      fi
      if [ -d "/var/lib/tomcat8" ]; then
        TOMCAT_DIR="/var/lib/tomcat8"
      fi
      if [ -d "/var/lib/tomcat9" ]; then
        TOMCAT_DIR="/var/lib/tomcat9"
      fi
  fi  
  echo "${TOMCAT_DIR}"
  exitProcess 0
}

main "$@"
