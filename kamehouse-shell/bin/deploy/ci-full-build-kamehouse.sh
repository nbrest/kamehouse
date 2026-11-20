#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptConfig() {
  EXEC_ALL_SERVERS_TIMEOUT=300
  DOCKER_BUILD_TIMEOUT="7200"
  RETRIES=5
  RETRY_WAIT=60
}

initScriptEnv() {
  PROJECT_DIR=${HOME}/.kamehouse/git/jenkins/kamehouse
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

  ciDeployKameHouseShell

  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/deploy/build-kamehouse.sh --ci-build
  checkCommandStatus "$?" "An error occurred building kamehouse"

  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/docker/docker-ci-integration-tests-trigger.sh
  checkCommandStatus "$?" "An error occurred running the integration and api collection tests"

  ciDeployKameHouseMobile

  execWithTimeout ${DOCKER_BUILD_TIMEOUT} ${HOME}/.kamehouse/opt/kamehouse-shell/bin/docker/docker-ci-rebuild-docker-image.sh -c
  checkCommandStatus "$?" "An error occurred rebuilding the docker image"

  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/deploy/sonarcloud-run-kamehouse.sh
  checkCommandStatus "$?" "An error occurred running the sonarcloud scan"
  
  execWithTimeout ${EXEC_ALL_SERVERS_TIMEOUT} "${HOME}/.kamehouse/opt/kamehouse-shell/bin/exec/exec-kamehouse-all-servers.sh" -s "docker/docker-upgrade-containers.sh"
  checkTimeoutCommandStatus "$?" "An error occurred upgrading the docker containers in all servers"

  execWithTimeout ${EXEC_ALL_SERVERS_TIMEOUT} "${HOME}/.kamehouse/opt/kamehouse-shell/bin/exec/exec-kamehouse-all-servers.sh" -s "deploy/deploy-kamehouse.sh" -a "-m shell --pull"
  checkTimeoutCommandStatus "$?" "An error occurred deploying kamehouse shell in all servers"  

  execWithTimeout ${EXEC_ALL_SERVERS_TIMEOUT} "${HOME}/.kamehouse/opt/kamehouse-shell/bin/exec/exec-kamehouse-all-servers.sh" -s "deploy/deploy-kamehouse.sh" -a "--pull"
  checkTimeoutCommandStatus "$?" "An error occurred deploying kamehouse in all servers"  

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
  mkdir -p ${HOME}/.kamehouse/git/jenkins
  cd ${HOME}/.kamehouse/git/jenkins
  execWithTimeout ${GIT_TIMEOUT} git clone https://github.com/nbrest/kamehouse.git
  cd kamehouse
  checkCommandStatus "$?" "Invalid kamehouse project root directory for jenkins `pwd`"
}

gitResetBranch() {
  if ! ${USE_CURRENT_DIR}; then
    log.info "Resetting dev branch"
    git reset --hard
    git checkout dev
    execWithTimeout ${GIT_TIMEOUT} git pull origin dev
    checkCommandStatus "$?" "Error pulling dev branch"
  else
    git checkout dev
    checkCommandStatus "$?" "Error checking out dev branch"
  fi
}

ciDeployKameHouseShell() {
  local NUM_RETRIES=$((RETRIES))
  while [ ${NUM_RETRIES} -gt 0 ]; do
    log.info "Retries left: ${COL_RED}${NUM_RETRIES}"
    ${HOME}/.kamehouse/opt/kamehouse-shell/bin/deploy/deploy-kamehouse.sh -m shell -c -l ${LOG_LEVEL}
    if [ "$?" == "0" ]; then
      log.info "Success! No more retries needed"
      return
    fi
    : $((NUM_RETRIES--))
    if [ ${NUM_RETRIES} -gt 0 ]; then 
      log.info "Sleeping ${RETRY_WAIT} seconds until next retry"
      sleep ${RETRY_WAIT}
    fi
  done  
}

ciDeployKameHouseMobile() {
  local NUM_RETRIES=$((RETRIES))
  while [ ${NUM_RETRIES} -gt 0 ]; do
    log.info "Retries left: ${COL_RED}${NUM_RETRIES}"
    ${HOME}/.kamehouse/opt/kamehouse-shell/bin/deploy/deploy-kamehouse.sh -m mobile -c
    if [ "$?" == "0" ]; then
      log.info "Success! No more retries needed"
      return
    fi
    : $((NUM_RETRIES--))
    if [ ${NUM_RETRIES} -gt 0 ]; then 
      log.info "Sleeping ${RETRY_WAIT} seconds until next retry"
      sleep ${RETRY_WAIT}
    fi
  done  
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
