#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/my.scripts/.cred/.cred

# Global variables
LOG_PROCESS_TO_FILE=true

mainProcess() {
  gitPullAllAllServers
}

gitPullAllAllServers() {
  # niko-server
  gitPullAll "niko-server" "80" "win" &
  gitPullAll "niko-server" "7080" "lin" &
  # niko-server-vm-ubuntu
  gitPullAll "niko-server-vm-ubuntu" "80" "lin" &
  gitPullAll "niko-server-vm-ubuntu" "7080" "lin" &
  # pi
  gitPullAll "pi" "80" "lin" &
  gitPullAll "pi" "7080" "lin" &
  # niko-nba
  gitPullAll "niko-nba" "80" "win" &
  # niko-w
  gitPullAll "niko-w" "80" "win" &
  # niko-w-vm-ubuntu
  gitPullAll "niko-w-vm-ubuntu" "80" "lin" &

  log.info "Waiting for git pull all to finish in all servers. ${COL_YELLOW}This process can take several minutes"
  wait
  log.info "${COL_RED}Finished git pull all in all servers"
}

gitPullAll() {
  local SERVER=$1
  local PORT=$2
  local HOST_OS=$3
  log.info "Started gitPullAll ${COL_PURPLE}${SERVER}:${PORT}:${HOST_OS}"
  executeScriptInServer ${SERVER} ${PORT} "${HOST_OS}/git/git-pull-all.sh"
  log.info "${COL_RED}Finished gitPullAll ${COL_CYAN}${SERVER}:${PORT}:${HOST_OS}"
}

executeScriptInServer() {
  local SERVER=$1
  local PORT=$2
  local SCRIPT=$3
  local URL="http://${SERVER}:${PORT}/kame-house-groot/api/v1/admin/my-scripts/exec-script.php?script=${SCRIPT}"
  log.debug "Executing request: ${COL_BLUE}${URL}"
  RESPONSE=`curl --max-time 1800 -k --location --request GET "${URL}" --header "Authorization: Basic ${GROOT_API_BASIC_AUTH}" 2>/dev/null`
  #echo "${RESPONSE}"
}

main "$@"
