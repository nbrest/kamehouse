#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  USE_CURRENT_DIR=true
}

mainProcess() {
  setKameHouseRootProjectDir
  # the whole build runs on JDK 21
  source ${HOME}/programs/kamehouse-shell/bin/deploy/set-java-home.sh --override --log
  log.info "Running sonarcloud scan. Run the kamehouse build before executing this script"
  log.trace "SONAR_TOKEN=${SONAR_TOKEN}"
  # the sonarqube plugin is only applied when the build runs with -Psonar
  ${GRADLE_WRAPPER} clean build sonar -Psonar --console=plain \
    -Dsonar.projectKey=nbrest_kamehouse \
    -Dsonar.organization=nbrest \
    -Dsonar.host.url=https://sonarcloud.io \
    -Dsonar.token="${SONAR_TOKEN}"
  checkCommandStatus "$?" "Error running sonarcloud scan" 
  cleanLogsInGitRepoFolder
  cleanUpKameHouseBuildArtifacts
}

main "$@"
