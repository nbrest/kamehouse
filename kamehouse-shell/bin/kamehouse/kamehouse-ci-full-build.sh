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
  runFullContinuousIntegrationBuild
}

runFullContinuousIntegrationBuild() {
  log.info "Started running full continuous integration build"
  cd ${HOME}/git/kamehouse
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/deploy-kamehouse.sh -m shell
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/build-kamehouse.sh
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-ci-integration-tests-trigger.sh
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/deploy-kamehouse.sh -m mobile
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-ci-rebuild-kamehouse.sh
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/sonarcloud-run-kamehouse.sh
  log.info "Finished running full continuous integration build"
}

main "$@"
