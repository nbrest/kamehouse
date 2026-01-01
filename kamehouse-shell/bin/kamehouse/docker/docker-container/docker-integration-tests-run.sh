#!/bin/bash

# Run this script inside the docker container to execute integration tests

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi
importFunctions ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh

initScriptEnv() {
  PROJECT_DIR=/home/${DOCKER_USERNAME}/git/kamehouse
  SUCCESS="SUCCESS EXECUTING INTEGRATION TESTS"
  ERROR="ERROR EXECUTING INTEGRATION TESTS"
}

mainProcess() {
  setKameHouseRootProjectDir
  log.trace "DOCKER_USERNAME=${DOCKER_USERNAME}"
  log.trace "PROJECT_DIR=${PROJECT_DIR}"
  
  /home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/deploy/build-kamehouse.sh -p ci -i
  BUILD_RESULT=$?

  BUILD_LOG=`tail -n 150 /home/${DOCKER_USERNAME}/logs/build-kamehouse.log`
  BUILD_LOG_OUTPUT=`cat /home/${DOCKER_USERNAME}/logs/build-kamehouse.log`
  echo -e "${BUILD_LOG}" | grep "BUILD SUCCESS" > /dev/null
  BUILD_LOG_RESULT=$?
  
  echo -e "${BUILD_LOG_OUTPUT}"
  if [ "${BUILD_RESULT}" == "0" ] && [ "${BUILD_LOG_RESULT}" == "0" ]; then
    echo "${SUCCESS}"
  else
    echo "${ERROR}"
  fi
}

main "$@"
