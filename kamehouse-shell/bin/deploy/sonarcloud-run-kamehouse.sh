#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  USE_CURRENT_DIR=true
}

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
