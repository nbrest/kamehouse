#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/kamehouse/docker-functions.sh
importKamehouse common/functions/kamehouse/docker/release/java11-release-functions.sh

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
}

initScriptEnv() {
  SERVICE="kamehouse-docker-${DOCKER_IMAGE_TAG}"
  SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/release/java11-release/java11-release-kamehouse-docker-run.sh"
  SERVICE_ARGS=""
}

mainProcess() {
  checkKeepAliveScriptsEnabled
  log.warn "User running this script needs ${COL_RED}sudo netstat${COL_DEFAULT_LOG} permissions"
  PID=`sudo netstat -nltp | grep ":${DOCKER_HTTP_PORT} " | awk '{print $7}' | cut -d '/' -f 1`
  if [ -z "${PID}" ]; then
    log.info "${SERVICE} not running. Starting it now"
    ${SERVICE_STARTUP} ${SERVICE_ARGS} &
  else
    log.info "${SERVICE} is currently running with pid ${COL_PURPLE}${PID}"
  fi
}

main "$@"
