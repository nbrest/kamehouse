#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=false
PROJECT_DIR=${HOME}/git/java.web.kamehouse

main() {
  displayLatestGitCommit
}

displayLatestGitCommit() {
  cd ${PROJECT_DIR}

  BUILD_DATE=`git log | head -n 3 | grep Date | cut -c 9-`
  BUILD_VERSION=`git log | head -n 3 | grep commit | cut -c 8-`
  BUILD_VERSION=${BUILD_VERSION:0:10}".."${BUILD_VERSION:(-10)} 
  echo "buildVersion=${BUILD_VERSION}"
  echo "buildDate=${BUILD_DATE}"
}

main "$@"
