#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 99
fi

DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP_PROD}
SERVICE="kamehouse-docker"
SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-run-kamehouse.sh"
DEFAULT_DOCKER_PROFILE="prod"
DOCKER_PROFILE="${DEFAULT_DOCKER_PROFILE}"

mainProcess() {
  checkKeepAliveScriptsEnabled
  netstat -ano | grep "LISTENING" | grep ":${DOCKER_PORT_HTTP} " | tail -n 1
  PID=`netstat -ano | grep "LISTENING" | grep ":${DOCKER_PORT_HTTP} " | tail -n 1 | awk '{print $5}'`
  if [ -z "${PID}" ]; then
    log.error "${SERVICE} not running. Starting it now"
    ${SERVICE_STARTUP} -p ${DOCKER_PROFILE} &
  else
    log.info "${SERVICE} with profile ${DOCKER_PROFILE} is currently running with pid ${COL_PURPLE}${PID}"
  fi
}

parseArguments() {
  parseDockerProfile "$@"
}

setEnvFromArguments() {
  setEnvForDockerProfile
}

printHelpOptions() {
  printDockerProfileOption
}

main "$@"
