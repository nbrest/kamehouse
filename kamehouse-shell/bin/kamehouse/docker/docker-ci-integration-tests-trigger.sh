#!/bin/bash

# Config in ci to run as: sudo -u [USERNAME] /home/[USERNAME]/programs/kamehouse-shell/bin/kamehouse/docker/docker-ci-integration-tests-trigger.sh
# Add to sudoers:
# jenkins ALL=(ALL) NOPASSWD: /home/[USERNAME]/programs/kamehouse-shell/bin/kamehouse/docker/docker-ci-integration-tests-trigger.sh

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

INTEGRATION_TESTS_SUCCESS_MESSAGE="SUCCESS EXECUTING INTEGRATION TESTS"
SCRIPT="kamehouse/docker/docker-integration-tests-run.sh"
DOCKER_PORT_HTTP=15080
DOCKER_CI_CREDENTIALS="${DOCKER_DEMO_GROOT_API_BASIC_AUTH}"
RETRIES=4
NUM_MAIN_PROCESS_RETRIES=""
NUM_TOMCAT_STARTUP_RETRIES=""
NUM_LOGIN_RETRIES=""
NUM_INTEGRATION_TESTS_RETRIES=""
CONTAINER_STARTUP_SUCCESSFUL=false
INTEGRATION_TESTS_SUCCESSFUL=false
MAIN_PROCESS_SUCCESSFUL=false

mainProcess() {
  log.info "Running kamehouse integration tests on a ci docker container"
  mainProcessLoop
  if ${MAIN_PROCESS_SUCCESSFUL}; then
    log.info "All done!"
    exit 0
  else
    log.error "Finished with errors. Integration tests didn't complete successfully"
    exit 1
  fi
}

mainProcessLoop() {
  log.info "Starting ci docker container"
  NUM_MAIN_PROCESS_RETRIES=$((RETRIES))
  while [ ${NUM_MAIN_PROCESS_RETRIES} -gt 0 ]; do
    log.info "Retries left to do the main process: ${COL_RED}${NUM_MAIN_PROCESS_RETRIES}"
    startCiDockerContainerLoop
    if ${CONTAINER_STARTUP_SUCCESSFUL}; then
      executeIntegrationTestsLoop
      stopCiDockerContainer
      if ${INTEGRATION_TESTS_SUCCESSFUL}; then
        log.info "Integration tests executed successfully!"
        MAIN_PROCESS_SUCCESSFUL=true
        NUM_MAIN_PROCESS_RETRIES=0
      else
        log.error "Error executing integration tests"
        MAIN_PROCESS_SUCCESSFUL=false
      fi
    else
      stopCiDockerContainer
      log.error "Container startup failed. Can't execute integration tests"
      MAIN_PROCESS_SUCCESSFUL=false
    fi
    : $((NUM_MAIN_PROCESS_RETRIES--))
    if [ ${NUM_MAIN_PROCESS_RETRIES} -gt 0 ]; then 
      sleep 15
    fi
  done
}

startCiDockerContainerLoop() {
  log.info "Starting ci docker container"
  NUM_TOMCAT_STARTUP_RETRIES=$((RETRIES))
  while [ ${NUM_TOMCAT_STARTUP_RETRIES} -gt 0 ]; do
    log.info "Retries left to start ci docker container: ${COL_RED}${NUM_TOMCAT_STARTUP_RETRIES}"
    stopCiDockerContainer
    startCiDockerContainer
    waitForTomcatStartup
    loginCheckLoop
    : $((NUM_TOMCAT_STARTUP_RETRIES--))
    if [ ${NUM_TOMCAT_STARTUP_RETRIES} -gt 0 ]; then 
      sleep 10
    fi
  done
}

stopCiDockerContainer() {
  log.info "Stopping ci docker container"
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-stop-kamehouse.sh -p ci > /dev/null
}

startCiDockerContainer() {
  log.info "Starting ci docker container"
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-run-kamehouse.sh -p ci > /dev/null &
}

waitForTomcatStartup() {
  log.info "Waiting for tomcat startup"
  sleep 90
}

loginCheckLoop() {
  log.info "Check if I can login to tomcat inside the ci docker container"
  NUM_LOGIN_RETRIES=$((RETRIES))
  while [ ${NUM_LOGIN_RETRIES} -gt 0 ]; do
    log.info "Retries left to login to ci docker container: ${COL_RED}${NUM_LOGIN_RETRIES}"
    loginCheck
    : $((NUM_LOGIN_RETRIES--))
    if [ ${NUM_LOGIN_RETRIES} -gt 0 ]; then
      sleep 45
    fi
  done   
}

loginCheck() {
  local URL="localhost:${DOCKER_PORT_HTTP}/kame-house/admin/server-management"
  log.info "Executing request to ${COL_PURPLE}${URL}"
  curl --max-time 1800 -k --request POST "localhost:${DOCKER_PORT_HTTP}/kame-house/logout" > /dev/null
  local CURL_RESPONSE=`curl --max-time 1800 -k --request GET "${URL}" --header "Authorization: Basic ${DOCKER_CI_CREDENTIALS}"`
  log.trace "CURL_RESPONSE ${CURL_RESPONSE}"
  echo ${CURL_RESPONSE} | grep '<title>KameHouse - Server Management</title>' > /dev/null
  local LOGIN_RESPONSE_CODE=$?
  if [ "${LOGIN_RESPONSE_CODE}" == "0" ]; then
    NUM_LOGIN_RETRIES=0
    NUM_TOMCAT_STARTUP_RETRIES=0
    CONTAINER_STARTUP_SUCCESSFUL=true
    log.info "Tomcat startup completed successfully. Can proceed to run integration tests now"
  else
    CONTAINER_STARTUP_SUCCESSFUL=false
    log.error "Login attempt unsuccessful"
  fi
}

executeIntegrationTestsLoop() {
  log.info "Running integration tests script in background to update hibernate_sequence value..."
  log.debug "Doing this because the first run always fails in create kamehouse user because the"
  log.debug "sequence has value 1 which is already used as id in the initial test data"
  executeIntegrationTests > /dev/null
  log.info "Executing integration tests in the ci docker container"
  NUM_INTEGRATION_TESTS_RETRIES=$((RETRIES))
  while [ ${NUM_INTEGRATION_TESTS_RETRIES} -gt 0 ]; do
    log.info "Retries left to execute integration tests: ${COL_RED}${NUM_INTEGRATION_TESTS_RETRIES}"
    executeIntegrationTests
    : $((NUM_INTEGRATION_TESTS_RETRIES--))
    if [ ${NUM_INTEGRATION_TESTS_RETRIES} -gt 0 ]; then
      sleep 20
    fi
  done
}

executeIntegrationTests() {
  local URL="http://localhost:${DOCKER_PORT_HTTP}/kame-house-groot/api/v1/admin/kamehouse-shell/exec-script.php?script=${SCRIPT}"
  log.info "Executing request to ${COL_PURPLE}${URL}"
  local CURL_RESPONSE=`curl --max-time 1800 -k --request GET "${URL}" --header "Authorization: Basic ${DOCKER_CI_CREDENTIALS}"`
  log.trace "CURL_RESPONSE ${CURL_RESPONSE}"
  echo ${CURL_RESPONSE} | grep "${INTEGRATION_TESTS_SUCCESS_MESSAGE}" > /dev/null
  local INTEGRATION_TESTS_RESULT="$?"
  log.trace "INTEGRATION_TESTS_RESULT ${INTEGRATION_TESTS_RESULT}"
  if [ "${INTEGRATION_TESTS_RESULT}" == "0" ]; then
    NUM_INTEGRATION_TESTS_RETRIES=0
    INTEGRATION_TESTS_SUCCESSFUL=true
    log.info "Completed integration tests successfully!"
  else
    INTEGRATION_TESTS_SUCCESSFUL=false
    log.error "Error executing integration tests"
  fi
}

main "$@"
