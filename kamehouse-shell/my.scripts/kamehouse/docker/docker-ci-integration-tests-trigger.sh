#!/bin/bash

# Config in ci to run as: sudo -u nbrest /home/nbrest/my.scripts/kamehouse/docker/docker-ci-integration-tests-trigger.sh

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/my.scripts/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

INTEGRATION_TESTS_SUCCESS_MESSAGE="SUCCESS EXECUTING INTEGRATION TESTS"
SCRIPT="kamehouse/docker/docker-integration-tests-run.sh"
DOCKER_PORT_HTTP=15080
DOCKER_CI_CREDENTIALS="YWRtaW46YWRtaW4="
RETRIES=3
NUM_TOMCAT_STARTUP_RETRIES=""
NUM_LOGIN_RETRIES=""
NUM_INTEGRATION_TESTS_RETRIES=""
CONTAINER_STARTUP_SUCCESSFUL=false
INTEGRATION_TESTS_SUCCESSFUL=false

mainProcess() {
  log.info "Running kamehouse integration tests on a ci docker container"
  startCiDockerContainerLoop
  if ${CONTAINER_STARTUP_SUCCESSFUL}; then
    executeIntegrationTestsLoop
    stopCiDockerContainer
    if ${INTEGRATION_TESTS_SUCCESSFUL}; then
      log.info "Integration tests executed successfully!"
      exit 0
    else
      log.error "Error executing integration tests"
      exit 1
    fi
  else
    stopCiDockerContainer
    log.error "Container startup failed. Can't execute integration tests"
    exit 1
  fi
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
  ${HOME}/my.scripts/kamehouse/docker/docker-stop-java-web-kamehouse.sh -p ci > /dev/null
}

startCiDockerContainer() {
  log.info "Starting ci docker container"
  ${HOME}/my.scripts/kamehouse/docker/docker-run-java-web-kamehouse.sh -p ci > /dev/null &
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
  local URL="http://localhost:${DOCKER_PORT_HTTP}/kame-house-groot/api/v1/admin/my-scripts/exec-script.php?script=${SCRIPT}"
  log.info "Executing request to ${COL_PURPLE}${URL}"
  local CURL_RESPONSE=`curl --max-time 1800 -k --request GET "${URL}" --header "Authorization: Basic ${DOCKER_CI_CREDENTIALS}"`
  #log.debug "CURL_RESPONSE ${CURL_RESPONSE}"
  echo ${CURL_RESPONSE} | grep "${INTEGRATION_TESTS_SUCCESS_MESSAGE}" > /dev/null
  local INTEGRATION_TESTS_RESULT="$?"
  #log.debug "INTEGRATION_TESTS_RESULT ${INTEGRATION_TESTS_RESULT}"
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