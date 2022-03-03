#!/bin/bash

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

PROJECT_DIR=/home/nbrest/git/java.web.kamehouse
SUCCESS="SUCCESS EXECUTING INTEGRATION TESTS"
ERROR="ERROR EXECUTING INTEGRATION TESTS"
DOCKER_PORT_HTTP=15080
DOCKER_CI_CREDENTIALS="YWRtaW46YWRtaW4="
SCRIPT="kamehouse/docker/docker-integration-tests-run.sh"

main() {
  URL="http://localhost:${DOCKER_PORT_HTTP}/kame-house-groot/api/v1/admin/my-scripts/exec-script.php?script=${SCRIPT}"

  log.info "Executing request to ${COL_PURPLE}${URL}"
  
  RESPONSE=`curl --max-time 1800 -k --location --request GET "${URL}" --header "Authorization: Basic ${DOCKER_CI_CREDENTIALS}"`
  
  RESPONSE_EVAL=`echo ${RESPONSE} | grep "SUCCESS EXECUTING INTEGRATION TESTS"`
  TESTS_RESULT="$?"
  
  stopDockerContainer

  if [ "${TESTS_RESULT}" == "0" ]; then
    log.info "Integration tests executed successfully!"
    exit 0
  else
    log.error "Output: ${OUTPUT}"
    log.error "Error executing integration tests. Check the logs inside the docker container to debug"
    exit 1
  fi
}

stopDockerContainer() {
  log.info "Stopping ci docker container"
  local CONTAINER=`docker container list | grep -e "java.web.kamehouse\\|\\/home\\/nbrest\\/docker" | grep ${DOCKER_PORT_HTTP} |  cut -d ' ' -f1`
  if [ -n "${CONTAINER}" ]; then 
    log.info "Stopping container ${COL_PURPLE}${CONTAINER}"
    docker stop ${CONTAINER}
  else
    log.warn "No kamehouse container running detected"
  fi
}

main "$@"
