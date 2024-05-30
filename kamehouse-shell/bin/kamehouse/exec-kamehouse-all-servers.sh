#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi
source ${HOME}/.kamehouse/.shell/.cred

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

SCRIPT=""
SCRIPT_ARGS=""
SCRIPT_LOG_MESSAGE=""

mainProcess() {
  setScriptLogMessage
  execInAllServers
}

setScriptLogMessage() {
  if [ -z "${SCRIPT_ARGS}" ]; then
    SCRIPT_LOG_MESSAGE="'${SCRIPT}' without args"
  else
    SCRIPT_LOG_MESSAGE="'${SCRIPT}' with args '${SCRIPT_ARGS}'"
  fi
}

execInAllServers() {
  # niko-server
  execInServer "niko-server" "80" "false" "false" &

  # niko-server-vm-ubuntu
  execInServer "niko-server-vm-ubuntu" "80" "true" "false" &
  execInServer "niko-server-vm-ubuntu" "${DOCKER_PORT_HTTP_DEMO}" "true" "false" &
  execInServer "niko-server-vm-ubuntu" "${DOCKER_PORT_HTTP_PROD}" "false" "false" &

  # pi
  execInServer "pi" "443" "false" "true" &
  
  # niko-nba
  execInServer "niko-nba" "80" "true" "false" &

  # niko-w
  execInServer "niko-w" "80" "true" "false" &

  # niko-w-vm-ubuntu
  execInServer "niko-w-vm-ubuntu" "80" "true" "false" &

  log.info "Waiting for ${SCRIPT_LOG_MESSAGE} to finish in ALL servers. ${COL_YELLOW}This process can take several minutes"
  wait
  log.info "${COL_RED}Finished ${SCRIPT_LOG_MESSAGE} in ALL servers"
}

execInServer() {
  local SERVER=$1
  local PORT=$2
  local USE_DOCKER_DEMO_CRED=$3
  local IS_HTTPS=$4
  log.info "Started ${SCRIPT_LOG_MESSAGE} in ${COL_PURPLE}${SERVER}:${PORT}"
  sendRequestToServer ${SERVER} ${PORT} ${USE_DOCKER_DEMO_CRED} ${IS_HTTPS} &
  wait
  log.info "${COL_RED}Finished ${SCRIPT_LOG_MESSAGE} in ${COL_CYAN}${SERVER}:${PORT}"
}

sendRequestToServer() {
  local SERVER=$1
  local PORT=$2
  local USE_DOCKER_DEMO_CRED=$3
  local IS_HTTPS=$4
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
  while getopts ":a:s:" OPT; do
    case $OPT in
    ("a")
      SCRIPT_ARGS=$OPTARG
      SCRIPT_ARGS=$(echo "$SCRIPT_ARGS" | sed -e "s#EXEC_SCRIPT_ALL_SERVERS_ARG_SPACE# #g")
      ;;
    ("s")
      SCRIPT=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  checkRequiredOption "-s" "${SCRIPT}"
}

printHelpOptions() {
  addHelpOption "-a (args)" "script args"
  addHelpOption "-s (script)" "script to execute" "r"
}

main "$@"
