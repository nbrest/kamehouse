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

initKameHouseShellEnv() {
  LOG_CMD_ARGS=false
}

mainProcess() {
  validateCommandLineArguments "$@"
  log.info "Executing script ${COL_PURPLE}'${BASE_PATH}${SCRIPT}'${COL_DEFAULT_LOG}"
  log.trace "script args ${COL_PURPLE}'${SCRIPT_ARGS}'"
  initEnv

  if ${EXECUTE_ON_DOCKER_HOST}; then
    executeRemote
  else
    executeLocal
  fi
}

initScriptEnv() {
  SCRIPT=""
  SCRIPT_ARGS=""
  BASE_PATH="${HOME}/programs/kamehouse-shell/bin/"
  REMOTE_BASE_PATH="\${HOME}/programs/kamehouse-shell/bin/"
  EXECUTE_ON_DOCKER_HOST=false
  IS_DAEMON=false
  IS_EXECUTABLE_ON_DOCKER_HOST=false
}

validateCommandLineArguments() {
  log.debug "Validating command line arguments"

  local SUBPATH_RX=.*\\.\\.\\/.*
  if [[ "$@" =~ ${SUBPATH_RX} ]]; then
    log.error "Command line arguments try to escape kamehouse shell base path. Can't procede to execute script"
    exitProcess ${EXIT_INVALID_ARG}
  fi
  
  if [[ "$@" == *[\`'!'#\$%^\&*()\<\>\|\;+]* ]]; then
    log.error "Invalid characters in command line arguments. Can't procede. ${SCRIPT} ${SCRIPT_ARGS}"
    exitProcess ${EXIT_INVALID_ARG}
  fi

  if [ ! -f "${BASE_PATH}/${SCRIPT}" ]; then
    log.error "Script ${SCRIPT} doesn't exist"
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

initEnv() {
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
  log.debug "IS_DAEMON ${IS_DAEMON}"
  log.debug "IS_EXECUTABLE_ON_DOCKER_HOST ${IS_EXECUTABLE_ON_DOCKER_HOST}"
  log.debug "EXECUTE_ON_DOCKER_HOST ${EXECUTE_ON_DOCKER_HOST}"
}

executeRemote() {
  local REMOTE_COMMAND="${REMOTE_BASE_PATH}${SCRIPT} ${SCRIPT_ARGS}"
  IS_REMOTE_LINUX_HOST=${IS_LINUX_DOCKER_HOST}
  SSH_USER="${DOCKER_HOST_USERNAME}"
  SSH_SERVER="${DOCKER_HOST_IP}"
  SSH_COMMAND="${REMOTE_COMMAND}"
  executeSshCommand --skip-exit-code-check
}

executeLocal() {
  local LOCAL_COMMAND="${BASE_PATH}${SCRIPT} ${SCRIPT_ARGS}"
  if ${IS_DAEMON}; then
    log.trace "${LOCAL_COMMAND} &"
    ${LOCAL_COMMAND} &
  else
    log.trace "${LOCAL_COMMAND}"
    ${LOCAL_COMMAND}  
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
      -a)
        SCRIPT_ARGS="${CURRENT_OPTION_ARG}"
        ;;
      --daemon)
        IS_DAEMON=true
        ;;
      --execute-on-docker-host)
        IS_EXECUTABLE_ON_DOCKER_HOST=true
        ;;       
      -s)
        SCRIPT="${CURRENT_OPTION_ARG}"
        ;;
      # I can't use parseInvalidArgument here because the script arg might start with "-"
    esac
  done    
}

setEnvFromArguments() {
  checkRequiredOption "-s" "${SCRIPT}"
}

printHelpOptions() {
  addHelpOption "-a [args]" "script args"
  addHelpOption "--daemon" "the script should run in the background without waiting for execution to end"
  addHelpOption "--execute-on-docker-host" "execute the specified script on the docker host, when control host is enabled"
  addHelpOption "-s [script]" "script to execute" "r"
}

main "$@"
