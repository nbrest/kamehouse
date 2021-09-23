#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
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
  KAMEHOUSE_CMD_JAR=`ls -l ${HOME}/programs/kamehouse-cmd/lib/kamehouse-cmd* | head -n 1`
  BUILD_DATE=`echo ${KAMEHOUSE_CMD_JAR} | awk '{print $6 " " $7 " " $8}'`
  BUILD_VERSION=`echo ${KAMEHOUSE_CMD_JAR} | awk '{print $9}'`
  BUILD_VERSION=${BUILD_VERSION##*/}
  BUILD_VERSION=`echo ${BUILD_VERSION} | cut -d'-' -f 3`
  echo "buildVersion=${BUILD_VERSION}"
  echo "buildDate=${BUILD_DATE}"
}

main "$@"
