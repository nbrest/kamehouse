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

LOG_CMD_ARGS=false
SCRIPT=""
SCRIPT_ARGS=""
BASE_PATH="${HOME}/programs/kamehouse-shell/bin/"
REMOTE_BASE_PATH="\${HOME}/programs/kamehouse-shell/bin/"
EXECUTE_ON_DOCKER_HOST=false
IS_EXECUTABLE_ON_DOCKER_HOST=false

mainProcess() {
  validateCommandLineArguments "$@"
  log.info "Executing script ${COL_PURPLE}'${BASE_PATH}${SCRIPT}'${COL_DEFAULT_LOG}"
  log.trace "script args ${COL_PURPLE}'${SCRIPT_ARGS}'"
  setupEnv

  if ${EXECUTE_ON_DOCKER_HOST}; then
    local REMOTE_COMMAND="${REMOTE_BASE_PATH}${SCRIPT} ${SCRIPT_ARGS}"
    if ${IS_LINUX_DOCKER_HOST}; then
      log.trace "ssh -o ServerAliveInterval=10 ${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP} -C \"${REMOTE_COMMAND}\""
      ssh -o ServerAliveInterval=10 ${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP} -C "${REMOTE_COMMAND}"
    else
      log.trace "ssh -o ServerAliveInterval=10 ${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP} -C \"${GIT_BASH} -c \\\"${REMOTE_COMMAND}\\\"\""
      ssh -o ServerAliveInterval=10 ${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP} -C "${GIT_BASH} -c \"${REMOTE_COMMAND}\""
    fi
  else
    local LOCAL_COMMAND="${BASE_PATH}${SCRIPT} ${SCRIPT_ARGS}"
    log.trace "${LOCAL_COMMAND}"
    ${LOCAL_COMMAND}  
  fi
}

validateCommandLineArguments() {
  log.info "Validating command line arguments"
  local SUBPATH_REGEX=.*\\.\\.\\/.*
  if [[ "$@" =~ ${SUBPATH_REGEX} ]]; then
    log.error "Command line arguments try to escape kamehouse shell base path. Can't procede to execute script"
    exitProcess ${EXIT_INVALID_ARG}
  fi
  if [[ "$@" == *[\`'!'#\$%^\&*()\<\>\|\;+]* ]]; then
    log.error "Invalid characters in command line arguments. Can't procede"
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

setupEnv() {
  if [ "${DOCKER_CONTROL_HOST}" == "true" ] && [ "${IS_EXECUTABLE_ON_DOCKER_HOST}" == "true" ]; then
    log.info "Executing script on docker host"
    EXECUTE_ON_DOCKER_HOST=true
  fi
  printEnv
}

printEnv() {
  log.debug "DOCKER_HOST_IP ${DOCKER_HOST_IP}"
  log.debug "DOCKER_HOST_HOSTNAME ${DOCKER_HOST_HOSTNAME}"
  log.debug "DOCKER_HOST_OS ${DOCKER_HOST_OS}"
  log.debug "DOCKER_HOST_USERNAME ${DOCKER_HOST_USERNAME}"
  log.debug "DOCKER_CONTROL_HOST ${DOCKER_CONTROL_HOST}"
  log.debug "IS_LINUX_DOCKER_HOST ${IS_LINUX_DOCKER_HOST}"
  log.debug "IS_EXECUTABLE_ON_DOCKER_HOST ${IS_EXECUTABLE_ON_DOCKER_HOST}"
  log.debug "EXECUTE_ON_DOCKER_HOST ${EXECUTE_ON_DOCKER_HOST}"
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
      -a)
        SCRIPT_ARGS="${CURRENT_OPTION_ARG}"
        ;;
      -s)
        SCRIPT="${CURRENT_OPTION_ARG}"
        ;;
      -x)
        IS_EXECUTABLE_ON_DOCKER_HOST=true
        ;;
      # I can't use parseInvalidArgument here because the script arg might start with "-"
    esac
  done    
}

setEnvFromArguments() {
  checkRequiredOption "-s" "${SCRIPT}"
}

printHelpOptions() {
  addHelpOption "-a (args)" "script args"
  addHelpOption "-s (script)" "script to execute" "r"
  addHelpOption "-x" "execute the specified script on the docker host, when control host is enabled"
}

main "$@"
