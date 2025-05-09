#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing docker-functions.sh" ; exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker/release/java8-release-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing java8-release-functions.sh" ; exit 99
fi

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
}

initScriptEnv() {
  SERVICE="kamehouse-docker-${DOCKER_IMAGE_TAG}"
  SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/release/java8-release/java8-release-kamehouse-docker-run.sh"
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

setEnvForFirstRelease() {
  if ${FIRST_RELEASE}; then
    DOCKER_HTTP_PORT=${FIRST_RELEASE_HTTP_PORT}
    DOCKER_IMAGE_TAG=${FIRST_RELEASE_IMAGE_TAG}
    SERVICE="kamehouse-docker-${DOCKER_IMAGE_TAG}"
    SERVICE_ARGS="-f"
  fi
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -f)
        FIRST_RELEASE=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  setEnvForFirstRelease
}

printHelpOptions() {
  addHelpOption "-f" "keep alive the docker image for the first release version"
}

main "$@"
