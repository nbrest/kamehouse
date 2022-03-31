#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/.kamehouse/.shell/.cred

# Global variables
LOG_PROCESS_TO_FILE=true

mainProcess() {
  gitPullAllAllServers
}

gitPullAllAllServers() {
  # niko-server
  gitPullAll "niko-server" "80" "win" "false" &

  # niko-server-vm-ubuntu
  gitPullAll "niko-server-vm-ubuntu" "80" "lin" "false" &
  gitPullAll "niko-server-vm-ubuntu" "12080" "lin" "true" &

  # pi
  gitPullAll "pi" "80" "lin" "false" &

  # niko-nba
  gitPullAll "niko-nba" "80" "win" "false" &

  # niko-w
  gitPullAll "niko-w" "80" "win" "false" &

  # niko-w-vm-ubuntu
  gitPullAll "niko-w-vm-ubuntu" "80" "lin" "false" &

  log.info "Waiting for git pull all to finish in all servers. ${COL_YELLOW}This process can take several minutes"
  wait
  log.info "${COL_RED}Finished git pull all in all servers"
}

gitPullAll() {
  local SERVER=$1
  local PORT=$2
  local HOST_OS=$3
  local IS_DOCKER_DEMO=$4
  log.info "Started gitPullAll ${COL_PURPLE}${SERVER}:${PORT}:${HOST_OS}"
  executeScriptInServer ${SERVER} ${PORT} ${IS_DOCKER_DEMO} "${HOST_OS}/git/git-pull-all.sh"
  log.info "${COL_RED}Finished gitPullAll ${COL_CYAN}${SERVER}:${PORT}:${HOST_OS}"
}

executeScriptInServer() {
  local SERVER=$1
  local PORT=$2
  local IS_DOCKER_DEMO=$3
  local SCRIPT=$4

  if ${IS_DOCKER_DEMO}; then
    BASIC_AUTH=${DOCKER_DEMO_GROOT_API_BASIC_AUTH}
  else
    BASIC_AUTH=${GROOT_API_BASIC_AUTH}
  fi

  local URL="http://${SERVER}:${PORT}/kame-house-groot/api/v1/admin/kamehouse-shell/exec-script.php?script=${SCRIPT}"
  log.debug "Executing request: ${COL_BLUE}${URL}"
  RESPONSE=`curl --max-time 1800 -k --location --request GET "${URL}" --header "Authorization: Basic ${BASIC_AUTH}" 2>/dev/null`
  #echo "${RESPONSE}"
}

main "$@"
