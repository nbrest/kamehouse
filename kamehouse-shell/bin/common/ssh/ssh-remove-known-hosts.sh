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

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 99
fi

KNOWN_KEY_TO_REMOVE=""

mainProcess() {
  removeServerKey
}

removeServerKey() {
  log.info "Listing ${KNOWN_KEY_TO_REMOVE} in known_hosts"
  cat "${HOME}/.ssh/known_hosts" | grep "${KNOWN_KEY_TO_REMOVE}"
  log.info "Removing ${KNOWN_KEY_TO_REMOVE} key from known hosts"
  log.debug "ssh-keygen -f \"${HOME}/.ssh/known_hosts\" -R \"${KNOWN_KEY_TO_REMOVE}\""
  ssh-keygen -f "${HOME}/.ssh/known_hosts" -R "${KNOWN_KEY_TO_REMOVE}"
}

parseArguments() {
  while getopts ":k:" OPT; do
    case $OPT in
    ("k")
      KNOWN_KEY_TO_REMOVE="$OPTARG"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done 
}

setEnvFromArguments() {
  checkRequiredOption "-k" "${KNOWN_KEY_TO_REMOVE}" 
}

printHelpOptions() {
  addHelpOption "-k key" "'hostname/ip' or '[hostname/ip]:port' to remove from known_hosts" "r"
}


main "$@"
