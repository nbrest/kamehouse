#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then echo "Error importing docker-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP_PROD}
  SERVICE="kamehouse-docker"
  SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-run-kamehouse.sh"
  DEFAULT_DOCKER_PROFILE="prod"
  DOCKER_PROFILE="${DEFAULT_DOCKER_PROFILE}"
}

mainProcess() {
  checkKeepAliveScriptsEnabled
  log.warn "User running this script needs ${COL_RED}sudo netstat${COL_DEFAULT_LOG} permissions"
  PID=`sudo netstat -nltp | grep ":${DOCKER_PORT_HTTP} " | awk '{print $7}' | cut -d '/' -f 1`
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
