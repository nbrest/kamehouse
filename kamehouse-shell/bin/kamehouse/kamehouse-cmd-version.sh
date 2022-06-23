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

main() {
  displayKameHouseCmdVersion
}

displayKameHouseCmdVersion() {
  KAMEHOUSE_CMD_JAR_LS=`ls -l ${HOME}/programs/kamehouse-cmd/lib/kamehouse-cmd* | head -n 1`
  BUILD_VERSION=`echo ${KAMEHOUSE_CMD_JAR_LS} | awk '{print $9}'`
  BUILD_VERSION=${BUILD_VERSION##*/}
  BUILD_VERSION=`echo ${BUILD_VERSION} | cut -d'-' -f 3`
  GIT_COMMIT_HASH=`cat ${HOME}/programs/kamehouse-cmd/lib/git-commit-hash.txt 2>/dev/null`
  if [ -n "${GIT_COMMIT_HASH}" ]; then
    BUILD_VERSION=${BUILD_VERSION}"-"${GIT_COMMIT_HASH}
  fi
  echo "buildVersion=${BUILD_VERSION}"
  KAMEHOUSE_CMD_JAR_FILE=`ls ${HOME}/programs/kamehouse-cmd/lib/kamehouse-cmd* | head -n 1`
  BUILD_DATE=`stat ${KAMEHOUSE_CMD_JAR_FILE} | grep "Modify"` 
  BUILD_DATE=${BUILD_DATE:8:19}
  echo "buildDate=${BUILD_DATE}"
}

main "$@"
