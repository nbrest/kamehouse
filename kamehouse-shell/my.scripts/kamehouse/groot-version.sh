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

  KAMEHOUSE_RELEASE_VERSION=`grep -e "<version>.*1-KAMEHOUSE-SNAPSHOT</version>" pom.xml | awk '{print $1}'`
  KAMEHOUSE_RELEASE_VERSION=`echo ${KAMEHOUSE_RELEASE_VERSION:9:6}`

  BUILD_VERSION=`git log | head -n 3 | grep commit | cut -c 8-`
  BUILD_VERSION=${BUILD_VERSION:0:8}
  if [ -n "${KAMEHOUSE_RELEASE_VERSION}" ]; then
    BUILD_VERSION=${KAMEHOUSE_RELEASE_VERSION}"-"${BUILD_VERSION}
  fi
  echo "buildVersion=${BUILD_VERSION}"

  BUILD_DATE=`git log | head -n 3 | grep Date | cut -c 9-`
  BUILD_DATE=${BUILD_DATE:0:25}
  BUILD_DATE=`date -d"$BUILD_DATE" +%Y-%m-%d' '%H:%M:%S`
  echo "buildDate=${BUILD_DATE}"
}

main "$@"
