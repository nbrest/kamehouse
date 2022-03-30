#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=true

main() {
  if [ "$1" == "-V" ]; then
    displayKameHouseCmdVersion
    exitProcess "0"
  fi

  # Execute the latest deployed version of kamehouse-cmd
  ${HOME}/programs/kamehouse-cmd/bin/kamehouse-cmd.sh "$@"
}

parseArguments() {
  # Override default. Skip parsing as it's done in kamehouse-cmd
  return
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
