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
source ${HOME}/.kamehouse/.shell/.cred

LOG_PROCESS_TO_FILE=true

mainProcess() {
  log.info "Running sonarcloud scan. Run the kamehouse build before executing this script"
  log.trace "SONAR_TOKEN=${SONAR_TOKEN}"
  mvn clean verify sonar:sonar -Dstyle.color=always -Dsonar.projectKey=nbrest_kamehouse -Dsonar.organization=nbrest -Dsonar.token=${SONAR_TOKEN}
  checkCommandStatus "$?" "Error running sonarcloud scan" 
}

main "$@"
