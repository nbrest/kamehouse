#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  PROJECT_DIR=${HOME}/git/jenkins/kamehouse
  LOG_LEVEL=INFO
}

mainProcess() {
  runFullContinuousIntegrationBuild
}

runFullContinuousIntegrationBuild() {
  log.info "Started running full continuous integration build"
  
  gitCloneKameHouse
  setKameHouseRootProjectDir
  gitResetBranch

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/deploy-kamehouse.sh -m shell -c -l ${LOG_LEVEL}
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
  
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/exec-kamehouse-all-servers.sh -s "kamehouse/docker/docker-upgrade-containers.sh"
  checkCommandStatus "$?" "An error occurred upgrading the docker containers in all servers"

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/exec-kamehouse-all-servers.sh -s "kamehouse/deploy-kamehouse.sh" -a "-m shell"
  checkCommandStatus "$?" "An error occurred deploying kamehouse shell in all servers"  

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/exec-kamehouse-all-servers.sh -s "kamehouse/deploy-kamehouse.sh"
  checkCommandStatus "$?" "An error occurred deploying kamehouse in all servers"  

  log.info "Finished running full continuous integration build"
}

gitCloneKameHouse() {
  if ${USE_CURRENT_DIR}; then
    return
  fi
  if [ -d "${PROJECT_DIR}" ]; then
    log.info "jenkins kamehouse repository already exists: ${COL_PURPLE}${PROJECT_DIR}"
    return
  fi
  log.info "Cloning kamehouse git repository into ${COL_PURPLE}${PROJECT_DIR}"
  mkdir -p ${HOME}/git/jenkins
  cd ${HOME}/git/jenkins
  git clone https://github.com/nbrest/kamehouse.git
  cd kamehouse
  checkCommandStatus "$?" "Invalid kamehouse project root directory for jenkins `pwd`"
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
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -c)
        USE_CURRENT_DIR=true
        ;;
      -l)
        LOG_LEVEL="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-c" "Run ci full build from current directory instead of from default directory."
  addHelpOption "-l [ERROR|WARN|INFO|DEBUG|TRACE]" "set log level for scripts. Default is INFO"
}

main "$@"
