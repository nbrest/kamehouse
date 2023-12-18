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

LOG_PROCESS_TO_FILE=true
PROJECT_DIR=${HOME}/git/jenkins/kamehouse

mainProcess() {
  runFullContinuousIntegrationBuild
}

runFullContinuousIntegrationBuild() {
  log.info "Started running full continuous integration build"
  
  gitCloneKameHouse
  setKameHouseRootProjectDir
  gitResetBranch

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/deploy-kamehouse.sh -m shell -c
  checkCommandStatus "$?" "An error occurred deploying kamehouse-shell"

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/build-kamehouse.sh
  checkCommandStatus "$?" "An error occurred building kamehouse"

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-ci-integration-tests-trigger.sh
  checkCommandStatus "$?" "An error occurred running the integration tests"

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/deploy-kamehouse.sh -m mobile -c
  checkCommandStatus "$?" "An error occurred deploying kamehouse-mobile"

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-ci-rebuild-docker-image.sh -c
  checkCommandStatus "$?" "An error occurred rebuilding the docker image"

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/sonarcloud-run-kamehouse.sh
  checkCommandStatus "$?" "An error occurred running the sonarcloud scan"

  log.info "Finished running full continuous integration build"
}

gitCloneKameHouse() {
  if ! ${USE_CURRENT_DIR}; then
    if [ ! -d "${PROJECT_DIR}" ]; then
      log.info "Cloning kamehouse git repository into ${COL_PURPLE}${PROJECT_DIR}"
      mkdir -p ${HOME}/git/jenkins
      cd ${HOME}/git/jenkins
      git clone https://github.com/nbrest/kamehouse.git
      cd kamehouse
      checkCommandStatus "$?" "Invalid kamehouse project root directory for jenkins `pwd`"
    else
      log.info "jenkins kamehouse repository already exists: ${COL_PURPLE}${PROJECT_DIR}"
    fi
  fi
}

gitResetBranch() {
  if ! ${USE_CURRENT_DIR}; then
    log.info "Resetting dev branch"
    git reset --hard
    git checkout dev
    git pull origin dev
    checkCommandStatus "$?" "Error pulling dev branch"
  else
    git checkout dev
    checkCommandStatus "$?" "Error checking out dev branch"
  fi
}

parseArguments() {
  while getopts ":c" OPT; do
    case $OPT in
    ("c")
      USE_CURRENT_DIR=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

printHelpOptions() {
  addHelpOption "-c" "Run ci full build from current directory instead of from default directory."
}

main "$@"
