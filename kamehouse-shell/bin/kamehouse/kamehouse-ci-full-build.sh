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
USE_CURRENT_DIR=false

mainProcess() {
  runFullContinuousIntegrationBuild
}

runFullContinuousIntegrationBuild() {
  log.info "Started running full continuous integration build"
  
  setWorkingDir

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

setWorkingDir() {
  if [ ! -d "./kamehouse-shell/bin" ] || [ ! -d "./.git" ]; then
    if ! ${USE_CURRENT_DIR}; then 
      gitCloneKameHouse
    else
      log.info "Running ci full build from directory ${COL_PURPLE}`pwd`"
    fi
  else
    log.info "Running ci full build from directory ${COL_PURPLE}`pwd`"
  fi
}

gitCloneKameHouse() {
  log.info "Cloning kamehouse git repository into ${COL_PURPLE}${HOME}/git/jenkins/kamehouse"
  mkdir -p ${HOME}/git/jenkins
  cd ${HOME}/git/jenkins

  if [ ! -d "./kamehouse" ]; then
    git clone https://github.com/nbrest/kamehouse.git
  else
    log.info "jenkins kamehouse repository already exists"
  fi

  cd kamehouse
  checkCommandStatus "$?" "Invalid project directory" 

  git reset --hard
  git checkout dev
  git pull origin dev
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
