#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
}

mainProcess() {
  if [ -z "${SSH_COMMAND}" ]; then
    sshToRemoteServer
  else
    executeSshCommand
  fi
}

sshToRemoteServer() {
  log.debug "ssh -p ${SSH_PORT} ${SSH_OPTIONS} ${SSH_USER}@${SSH_SERVER}"
  ssh -p ${SSH_PORT} ${SSH_OPTIONS} ${SSH_USER}@${SSH_SERVER}
}

parseArguments() {
  parseKameHouseServer "$@"

  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -z)
        # parsed in a previous parse options function 
        ;;
      -c)
        SSH_COMMAND="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  setEnvForKameHouseServer
}

printHelpOptions() {
  addHelpOption "-c command" "command to execute in the remote shell"
  printKameHouseServerOption
}

main "$@"
