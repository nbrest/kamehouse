#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

LOG_PROCESS_TO_FILE=false

mainProcess() {
  if [ -z "${SSH_COMMAND}" ]; then
    sshToRemoteServer
  else
    executeSshCommand
  fi
}

sshToRemoteServer() {
  log.debug "ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER}"
  ssh -p ${SSH_PORT} -t -o ServerAliveInterval=10 ${SSH_USER}@${SSH_SERVER}
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
