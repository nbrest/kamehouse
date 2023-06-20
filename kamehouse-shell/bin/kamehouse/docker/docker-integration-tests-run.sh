#!/bin/bash

# Run this script inside the docker container to execute integration tests

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

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 1
fi

export PATH="${HOME}/programs/apache-maven/bin:${PATH}"

PROJECT_DIR=/home/${DOCKER_USERNAME}/git/kamehouse
SUCCESS="SUCCESS EXECUTING INTEGRATION TESTS"
ERROR="ERROR EXECUTING INTEGRATION TESTS"

mainProcess() {
  log.trace "DOCKER_USERNAME=${DOCKER_USERNAME}"
  log.trace "PROJECT_DIR=${PROJECT_DIR}"
  cd ${PROJECT_DIR}
  
  /home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/build-kamehouse.sh -p ci -i > /home/${DOCKER_USERNAME}/logs/build-kamehouse.log
  BUILD_RESULT=$?

  BUILD_LOG=`tail -n 150 /home/${DOCKER_USERNAME}/logs/build-kamehouse.log`
  echo -e "${BUILD_LOG}" | grep "BUILD SUCCESS" > /dev/null
  BUILD_LOG_RESULT=$?
  
  echo -e "${BUILD_LOG}"
  if [ "${BUILD_RESULT}" == "0" ] && [ "${BUILD_LOG_RESULT}" == "0" ]; then
    echo "${SUCCESS}"
  else
    echo "${ERROR}"
  fi
}

main "$@"
