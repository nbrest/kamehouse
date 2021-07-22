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
  gitPullAll "niko-server" "win" &
  gitPullAll "niko-server-vm-ubuntu" "lin" &
  gitPullAll "pi" "lin" &
  gitPullAll "niko-nba" "win" &
  gitPullAll "niko-w" "win" &
  gitPullAll "niko-w-vm-ubuntu" "lin" &

  log.info "Waiting for git pull all to finish in all servers. ${COL_YELLOW}This process can take several minutes"
  wait
  log.info "${COL_RED}Finished git pull all in all servers"
}

gitPullAll() {
  local SERVER=$1
  local HOST_OS=$2
  log.info "Started gitPullAll ${COL_PURPLE}${SERVER}"
  executeScriptInServer ${SERVER} "${HOST_OS}/git/git-pull-all.sh"
  log.info "${COL_RED}Finished gitPullAll ${COL_CYAN}${SERVER}"
}

executeScriptInServer() {
  local SERVER=$1
  local SCRIPT=$2
  RESPONSE=`curl --max-time 1800 -k --location --request GET "http://${SERVER}/kame-house-groot/api/v1/admin/my-scripts/exec-script.php?script=${SCRIPT}" --header "Authorization: Basic ${ROOT_API_BASIC_AUTH}" 2>/dev/null`
  #echo "${RESPONSE}"
}

main "$@"
