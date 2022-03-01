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

# Global variables
SCRIPT=""
SCRIPT_ARGS=""
BASE_PATH="${HOME}/my.scripts/"
REMOTE_BASE_PATH="\$HOME/my.scripts/"
CONTAINER_ENV_FILE="${HOME}/.kamehouse/.kamehouse-docker-container-env"
EXECUTE_ON_DOCKER_HOST=false
IS_EXECUTABLE_ON_DOCKER_HOST=false

mainProcess() {
  log.info "Executing script ${BASE_PATH}${SCRIPT} with arguments ${SCRIPT_ARGS}"
  setupEnv

  if ${EXECUTE_ON_DOCKER_HOST}; then
    if ${IS_LINUX_DOCKER_HOST}; then
      ssh -o ServerAliveInterval=10 ${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP} -C "${REMOTE_BASE_PATH}${SCRIPT} ${SCRIPT_ARGS}"
    else
      ssh -o ServerAliveInterval=10 ${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP} -C "git-bash -c \"${REMOTE_BASE_PATH}${SCRIPT} ${SCRIPT_ARGS}\""
    fi
  else
    ${BASE_PATH}${SCRIPT} ${SCRIPT_ARGS}  
  fi
}

setupEnv() {
  if [ -f "${CONTAINER_ENV_FILE}" ]; then
    #log.debug "Running inside a docker container"
    source ${CONTAINER_ENV_FILE}
  fi

  if ${DOCKER_CONTROL_HOST} && ${IS_EXECUTABLE_ON_DOCKER_HOST}; then
    EXECUTE_ON_DOCKER_HOST=true
  fi
  #printEnv
}

printEnv() {
  log.debug "DOCKER_HOST_IP ${DOCKER_HOST_IP}"
  log.debug "DOCKER_HOST_USERNAME ${DOCKER_HOST_USERNAME}"
  log.debug "DOCKER_HOST_OS ${DOCKER_HOST_OS}"
  log.debug "DOCKER_HOST_USERNAME ${DOCKER_HOST_USERNAME}"
  log.debug "DOCKER_CONTROL_HOST ${DOCKER_CONTROL_HOST}"
  log.debug "IS_LINUX_DOCKER_HOST ${IS_LINUX_DOCKER_HOST}"
  log.debug "IS_EXECUTABLE_ON_DOCKER_HOST ${IS_EXECUTABLE_ON_DOCKER_HOST}"
  log.debug "EXECUTE_ON_DOCKER_HOST ${EXECUTE_ON_DOCKER_HOST}"
}

parseArguments() {
  while getopts ":a:hs:x" OPT; do
    case $OPT in
    ("a")
      SCRIPT_ARGS=$OPTARG
      ;;
    ("h")
      printHelp
      exitProcess 0
      ;;
    ("s")
      SCRIPT=$OPTARG
      ;;
    ("x")
      IS_EXECUTABLE_ON_DOCKER_HOST=true
      ;;
    (\?)
      log.error "Invalid option: -$OPTARG"
      printHelp
      exitProcess 1
      ;;
    esac
  done

  if [ -z "${SCRIPT}" ]; then
    log.error "Option -s script is required"
    printHelp
    exitProcess 1
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"
  echo -e "     ${COL_BLUE}-a (args)${COL_NORMAL} script args"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-s (script)${COL_NORMAL} script to execute [${COL_RED}required${COL_NORMAL}]"
  echo -e "     ${COL_BLUE}-x${COL_NORMAL} execute the specified script on the docker host, when control host is enabled"
}

main "$@"