#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/.kamehouse/.shell/.cred

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=true

mainProcess() {
  deployInAllServers
}

deployInAllServers() {
  # niko-server
  deployInServer "niko-server" "80" "win" "false" "false" &

  # niko-server-dev-vm-ubuntu
  deployInServer "niko-server-dev-vm-ubuntu" "80" "lin" "true" "false" &

  # niko-server-vm-ubuntu
  deployInServer "niko-server-vm-ubuntu" "80" "lin" "true" "false" &
  deployInServer "niko-server-vm-ubuntu" "${DOCKER_PORT_HTTP_DEMO}" "lin" "true" "false" &

  # pi
  deployInServer "pi" "443" "lin" "false" "true" &
  
  # niko-nba
  deployInServer "niko-nba" "80" "win" "true" "false" &

  # niko-w
  deployInServer "niko-w" "80" "win" "true" "false" &

  # niko-w-vm-ubuntu
  deployInServer "niko-w-vm-ubuntu" "80" "lin" "true" "false" &

  log.info "Waiting for deployment to finish in all servers. ${COL_YELLOW}This process can take several minutes"
  wait
  log.info "${COL_RED}Finished deploying in all servers"
}

deployInServer() {
  local SERVER=$1
  local PORT=$2
  local HOST_OS=$3
  local USE_DOCKER_DEMO_CRED=$4
  local IS_HTTPS=$5
  log.info "Started deployInServer ${COL_PURPLE}${SERVER}:${PORT}:${HOST_OS}"
  deployKamehouse ${SERVER} ${PORT} ${USE_DOCKER_DEMO_CRED} ${IS_HTTPS} &
  wait
  log.info "${COL_RED}Finished deployInServer ${COL_CYAN}${SERVER}:${PORT}:${HOST_OS}"
}

deployKamehouse() {
  local SERVER=$1
  local PORT=$2
  local USE_DOCKER_DEMO_CRED=$3
  local IS_HTTPS=$4
  local SCRIPT_ARGS=""
  if [ -n "${MODULE_SHORT}" ]; then
    SCRIPT_ARGS="${SCRIPT_ARGS} -m ${MODULE_SHORT}"
  fi
  log.info "Started deployKamehouse ${COL_PURPLE}${SERVER}:${PORT}"
  executeScriptInServer ${SERVER} ${PORT} ${USE_DOCKER_DEMO_CRED} "kamehouse/deploy-kamehouse.sh" "${SCRIPT_ARGS}" ${IS_HTTPS}
  log.info "Finished deployKamehouse ${COL_PURPLE}${SERVER}:${PORT}"
}

executeScriptInServer() {
  local SERVER=$1
  local PORT=$2
  local USE_DOCKER_DEMO_CRED=$3
  local SCRIPT=$4
  local SCRIPT_ARGS="$5"
  local IS_HTTPS=$6
  local URL=""
  local URL_ENCODED_PARAMS=""
  local BASIC_AUTH=""
  local PROTOCOL="http"

  if ${IS_HTTPS}; then
    PROTOCOL="https"
  fi

  if [ -n "${SCRIPT_ARGS}" ]; then 
    URL_ENCODED_PARAMS="script=${SCRIPT}&args="$(urlencode "${SCRIPT_ARGS}")
  else
    URL_ENCODED_PARAMS="script=${SCRIPT}"
  fi

  if ${USE_DOCKER_DEMO_CRED}; then
    BASIC_AUTH=${DOCKER_DEMO_GROOT_API_BASIC_AUTH}
  else
    BASIC_AUTH=${GROOT_API_BASIC_AUTH}
  fi

  URL="${PROTOCOL}://${SERVER}:${PORT}/kame-house-groot/api/v1/admin/kamehouse-shell/execute.php?${URL_ENCODED_PARAMS}"
  log.info "Executing request: ${COL_BLUE}${URL}"
  RESPONSE=`curl --max-time 1800 -k --location --request GET "${URL}" --header "Authorization: Basic ${BASIC_AUTH}" 2>/dev/null`
  log.trace "${RESPONSE}"
}

parseArguments() {
  parseKameHouseModule "$@"
}

setEnvFromArguments() {
  setEnvForKameHouseModule
}

printHelpOptions() {
  printKameHouseModuleOption "deploy"
}

main "$@"
