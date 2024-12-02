#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi
loadKamehouseShellPwd

USE_CURRENT_DIR=true

mainProcess() {
  setKameHouseRootProjectDir
  log.info "Running sonarcloud scan. Run the kamehouse build before executing this script"
  log.trace "SONAR_TOKEN=${SONAR_TOKEN}"
  mvn clean verify sonar:sonar -Dstyle.color=always -Dsonar.projectKey=nbrest_kamehouse -Dsonar.organization=nbrest -Dsonar.token=${SONAR_TOKEN}
  checkCommandStatus "$?" "Error running sonarcloud scan" 
  cleanLogsInGitRepoFolder
  cleanUpMavenRepository
}

main "$@"
