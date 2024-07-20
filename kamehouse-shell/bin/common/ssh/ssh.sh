#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

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
  
  while getopts ":c:z:" OPT; do
    case $OPT in
    "c")
      SSH_COMMAND="$OPTARG"
      ;;
    \?)
      parseInvalidArgument "$OPTARG"
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
