#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/keep-alive/keep-alive-functions.sh
importKamehouse common/functions/kamehouse/docker-functions.sh

initScriptEnv() {
  KEEP_ALIVE_SERVICE="kamehouse-docker"
  KEEP_ALIVE_SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-run-kamehouse.sh"
  DEFAULT_DOCKER_PROFILE="prod"
  DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP_PROD}
  DOCKER_PROFILE="${DEFAULT_DOCKER_PROFILE}"
}

setKeepAliveServicePidLin() {
  logNeedsSudoPermissions netstat
  KEEP_ALIVE_SERVICE_PID=`sudo netstat -nltp | grep ":${DOCKER_PORT_HTTP} " | awk '{print $7}' | cut -d '/' -f 1`
}

setKeepAliveServicePidWin() {
  netstat -ano | grep "LISTENING" | grep ":${DOCKER_PORT_HTTP} " | tail -n 1
  KEEP_ALIVE_SERVICE_PID=`netstat -ano | grep "LISTENING" | grep ":${DOCKER_PORT_HTTP} " | tail -n 1 | awk '{print $5}'`
}

parseArguments() {
  parseDockerProfile "$@"
}

setEnvFromArguments() {
  setEnvForDockerProfile
  KEEP_ALIVE_SERVICE="${KEEP_ALIVE_SERVICE} with profile ${DOCKER_PROFILE}"
  KEEP_ALIVE_SERVICE_STARTUP="${KEEP_ALIVE_SERVICE_STARTUP} -p ${DOCKER_PROFILE}"
}

printHelpOptions() {
  printDockerProfileOption
}

main "$@"
