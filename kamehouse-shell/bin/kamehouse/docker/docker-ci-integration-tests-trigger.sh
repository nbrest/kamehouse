#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi
importFunctions ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  INTEGRATION_TESTS_SUCCESS_MESSAGE="SUCCESS EXECUTING INTEGRATION TESTS"
  SCRIPT="kamehouse/docker/docker-container/docker-integration-tests-run.sh"
  RETRIES=5
  NUM_MAIN_PROCESS_RETRIES=""
  NUM_TOMCAT_STARTUP_RETRIES=""
  NUM_LOGIN_RETRIES=""
  NUM_INTEGRATION_TESTS_RETRIES=""
  CONTAINER_STARTUP_SUCCESSFUL=false
  INTEGRATION_TESTS_SUCCESSFUL=false
  MAIN_PROCESS_SUCCESSFUL=false
}

mainProcess() {
  log.info "Running kamehouse integration tests on a ci docker container"
  mainProcessLoop
  if ${MAIN_PROCESS_SUCCESSFUL}; then
    log.info "All done!"
    exitSuccessfully
  else
    log.error "Finished with errors. Integration tests didn't complete successfully"
    exitProcess ${EXIT_ERROR}
  fi
}

mainProcessLoop() {
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
      local SECONDS=15
      log.info "Sleeping ${SECONDS} seconds until next retry"
      sleep ${SECONDS}
    fi
  done
}

startCiDockerContainerLoop() {
  log.info "Starting ci docker container loop"
  NUM_TOMCAT_STARTUP_RETRIES=$((RETRIES))
  while [ ${NUM_TOMCAT_STARTUP_RETRIES} -gt 0 ]; do
    log.info "Retries left to start ci docker container: ${COL_RED}${NUM_TOMCAT_STARTUP_RETRIES}"
    stopCiDockerContainer
    startCiDockerContainer
    waitForTomcatStartup
    loginCheckLoop
    : $((NUM_TOMCAT_STARTUP_RETRIES--))
    if [ ${NUM_TOMCAT_STARTUP_RETRIES} -gt 0 ]; then 
      local SECONDS=10
      log.info "Sleeping ${SECONDS} seconds until next retry"
      sleep ${SECONDS}
    fi
  done
}

stopCiDockerContainer() {
  log.info "Stopping ci docker container"
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-stop-kamehouse.sh -p ci
  killCiContainerProcesses
}

killCiContainerProcesses() {
  if ! ${KAMEHOUSE_KILL_CI_CONTAINER_PROCESSES}; then
    log.info "Skip killing ci container processes"
    return
  fi
  log.info "This script uses ${COL_RED}sudo${COL_DEFAULT_LOG} for netstat and kill"
  killProcessRunningOnPort "${DOCKER_PORT_SSH_CI}"
  killProcessRunningOnPort "${DOCKER_PORT_HTTP_CI}"
  killProcessRunningOnPort "${DOCKER_PORT_HTTPS_CI}"
  killProcessRunningOnPort "${DOCKER_PORT_TOMCAT_DEBUG_CI}"
  killProcessRunningOnPort "${DOCKER_PORT_TOMCAT_CI}"
  killProcessRunningOnPort "${DOCKER_PORT_MARIADB_CI}"
  killProcessRunningOnPort "${DOCKER_PORT_CMD_LINE_DEBUG_CI}"
}

killProcessRunningOnPort() {
  if ! ${IS_LINUX_HOST}; then
    return
  fi
  local PORT=$1
  local PID=`sudo netstat -nltp | grep ":${PORT}" | grep -v tcp6 | awk '{print $7}' | cut -d '/' -f 1`
  if [ -n "${PID}" ]; then
    log.info "Killing process ${PID} running on port ${PORT}"
    sudo kill -9 ${PID}
  else 
    log.debug "No process running on port ${PORT}"
  fi
}

startCiDockerContainer() {
  log.info "Starting ci docker container"
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-run-kamehouse.sh -p ci &
}

waitForTomcatStartup() {
  local SECONDS=90
  log.info "Waiting ${SECONDS} seconds for tomcat startup"
  sleep ${SECONDS}
  log.info "Checking if tomcat startup completed"
}

loginCheckLoop() {
  log.info "Check if I can login to tomcat inside the ci docker container"
  NUM_LOGIN_RETRIES=7
  while [ ${NUM_LOGIN_RETRIES} -gt 0 ]; do
    log.info "Retries left to login to ci docker container: ${COL_RED}${NUM_LOGIN_RETRIES}"
    loginCheck
    : $((NUM_LOGIN_RETRIES--))
    if [ ${NUM_LOGIN_RETRIES} -gt 0 ]; then
      local SECONDS=45
      log.info "Sleeping ${SECONDS} seconds until next retry"
      sleep ${SECONDS}
    fi
  done   
}

loginCheck() {
  log.trace "Executing logout from kamehouse"
  curl --max-time 60 -k --request POST "http://localhost:${DOCKER_PORT_HTTP_CI}/kame-house-auth/logout" > /dev/null

  local URL="http://localhost:${DOCKER_PORT_HTTP_CI}/kame-house/admin/server-management"
  log.info "Executing request to ${COL_PURPLE}${URL}"
  local CURL_RESPONSE=`curl --max-time 60 -k --request GET "${URL}" --header "Authorization: Basic ${DOCKER_DEMO_GROOT_API_BASIC_AUTH}"`
  log.trace "${COL_CYAN}---------- ${URL} response start"
  log.trace "${CURL_RESPONSE}" --log-message-only
  log.trace "${COL_CYAN}---------- ${URL} response end"
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
      local SECONDS=60
      log.info "Sleeping ${SECONDS} seconds until next retry"
      sleep ${SECONDS}
    fi
  done
}

executeIntegrationTests() {
  local URL="http://localhost:${DOCKER_PORT_HTTP_CI}/kame-house-groot/api/v1/admin/kamehouse-shell/execute.php?script=${SCRIPT}"
  log.info "Executing request to ${COL_PURPLE}${URL}"
  local CURL_RESPONSE=`curl --max-time 1200 -k --request GET "${URL}" --header "Authorization: Basic ${DOCKER_DEMO_GROOT_API_BASIC_AUTH}"`
  echo ${CURL_RESPONSE} | grep "${INTEGRATION_TESTS_SUCCESS_MESSAGE}" > /dev/null
  local INTEGRATION_TESTS_RESULT="$?"
  log.trace "INTEGRATION_TESTS_RESULT ${INTEGRATION_TESTS_RESULT}"
  if [ "${INTEGRATION_TESTS_RESULT}" == "0" ]; then
    NUM_INTEGRATION_TESTS_RETRIES=0
    INTEGRATION_TESTS_SUCCESSFUL=true
    log.trace "${COL_CYAN}---------- Success ${URL} response start"
    log.trace "${CURL_RESPONSE}" --log-message-only
    log.trace "${COL_CYAN}---------- Success ${URL} response end"
    log.info "Completed integration tests successfully!"
  else
    INTEGRATION_TESTS_SUCCESSFUL=false
    log.error "${COL_CYAN}---------- Error ${URL} response start"
    log.error "${CURL_RESPONSE}" --log-message-only
    log.error "${COL_CYAN}---------- Error ${URL} response end"
    log.error "Error executing integration tests"
  fi
}

main "$@"
