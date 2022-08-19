#!/bin/bash

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

# Global variables
SCRIPT=""
SCRIPT_ARGS=""
BASE_PATH="${HOME}/programs/kamehouse-shell/bin/"
REMOTE_BASE_PATH="\${HOME}/programs/kamehouse-shell/bin/"
EXECUTE_ON_DOCKER_HOST=false
IS_EXECUTABLE_ON_DOCKER_HOST=false
REQUIRES_SUDO=false
# Add these scripts to sudoers file to execute without password prompt
SUDO_SCRIPTS="
  kamehouse/httpd-startup.sh
  lin/kamehouse/httpd-status.sh
  lin/kamehouse/httpd-stop.sh
  lin/shutdown/reboot.sh
"

mainProcess() {
  log.info "Executing script ${BASE_PATH}${SCRIPT} with arguments ${SCRIPT_ARGS}"
  setupEnv

  if ${EXECUTE_ON_DOCKER_HOST}; then
    local REMOTE_COMMAND="${REMOTE_BASE_PATH}${SCRIPT} ${SCRIPT_ARGS}"
    if ${REQUIRES_SUDO}; then
      REMOTE_COMMAND="sudo "${REMOTE_COMMAND}
    fi
    if ${IS_LINUX_DOCKER_HOST}; then
      log.debug "ssh -o ServerAliveInterval=10 ${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP} -C \"${REMOTE_COMMAND}\""
      ssh -o ServerAliveInterval=10 ${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP} -C "${REMOTE_COMMAND}"
    else
      log.debug "ssh -o ServerAliveInterval=10 ${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP} -C \"${GIT_BASH} -c \\\"${REMOTE_COMMAND}\\\"\""
      ssh -o ServerAliveInterval=10 ${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP} -C "${GIT_BASH} -c \"${REMOTE_COMMAND}\""
    fi
  else
    local LOCAL_COMMAND="${BASE_PATH}${SCRIPT} ${SCRIPT_ARGS}"
    if ${REQUIRES_SUDO}; then
      LOCAL_COMMAND="sudo "${LOCAL_COMMAND}
    fi
    log.debug "${LOCAL_COMMAND}"
    ${LOCAL_COMMAND}  
  fi
}

setupEnv() {
  loadDockerContainerEnv

  if [ "${DOCKER_CONTROL_HOST}" == "true" ] && [ "${IS_EXECUTABLE_ON_DOCKER_HOST}" == "true" ]; then
    log.info "Executing script on docker host"
    EXECUTE_ON_DOCKER_HOST=true
  fi
  setRequiresSudo
  printEnv
}

setRequiresSudo() {
  if [ "${IS_LINUX_HOST}" == "true" ] || [[ "${EXECUTE_ON_DOCKER_HOST}" == "true" &&  "${IS_LINUX_DOCKER_HOST}" == "true" ]]; then
    echo ${SUDO_SCRIPTS} | grep ${SCRIPT} > /dev/null
    local RESULT=$?
    if [ "${RESULT}" == "0" ]; then
      log.debug "The script ${SCRIPT} requires sudo permissions"
      REQUIRES_SUDO=true
    else
      log.debug "The script ${SCRIPT} doesn't require sudo permissions"
    fi
  fi 
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
  log.debug "REQUIRES_SUDO ${REQUIRES_SUDO}"
}

parseArguments() {
  while getopts ":a:s:x" OPT; do
    case $OPT in
    ("a")
      SCRIPT_ARGS=$OPTARG
      ;;
    ("s")
      SCRIPT=$OPTARG
      ;;
    ("x")
      IS_EXECUTABLE_ON_DOCKER_HOST=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  if [ -z "${SCRIPT}" ]; then
    log.error "Option -s script is required"
    printHelp
    exitProcess 1
  fi
}

printHelpOptions() {
  addHelpOption "-a (args)" "script args"
  addHelpOption "-s (script)" "script to execute [${COL_RED}required${COL_NORMAL}]"
  addHelpOption "-x" "execute the specified script on the docker host, when control host is enabled"
}

main "$@"
