#!/bin/bash

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
loadKamehouseSecrets

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
  while read KAMEHOUSE_CONFIG_ENTRY; do
    if [ -n "${KAMEHOUSE_CONFIG_ENTRY}" ]; then
      local KAMEHOUSE_CONFIG_ENTRY_SPLIT=$(echo ${KAMEHOUSE_CONFIG_ENTRY} | tr "," "\n")
      local KAMEHOUSE_CONFIG=()
      while read KAMEHOUSE_CONFIG_ENTRY_FIELD; do
        if [ -n "${KAMEHOUSE_CONFIG_ENTRY_FIELD}" ]; then
          KAMEHOUSE_CONFIG+=("${KAMEHOUSE_CONFIG_ENTRY_FIELD}")
        fi
      done <<< ${KAMEHOUSE_CONFIG_ENTRY_SPLIT}
      local SERVER="${KAMEHOUSE_CONFIG[0]}"
      local PORT="${KAMEHOUSE_CONFIG[2]}"
      local IS_HTTPS=false
      if [ "${KAMEHOUSE_CONFIG[3]}" == "--https" ]; then
        IS_HTTPS=true
      fi
      local USE_DOCKER_DEMO_CRED=false
      if [ "${KAMEHOUSE_CONFIG[4]}" == "--use-docker-demo-groot-auth" ]; then
        USE_DOCKER_DEMO_CRED=true
      fi
      execInServer "${SERVER}" "${PORT}" "${IS_HTTPS}" "${USE_DOCKER_DEMO_CRED}" &
    fi
  done <<< ${KAMEHOUSE_SERVER_CONFIGS} 

  log.info "Waiting for ${SCRIPT_LOG_MESSAGE} to finish in ALL servers. ${COL_YELLOW}This process can take several minutes"
  wait
  log.info "${COL_RED}Finished ${SCRIPT_LOG_MESSAGE} in ALL servers"
}

execInServer() {
  local SERVER=$1
  local PORT=$2
  local IS_HTTPS=$3
  local USE_DOCKER_DEMO_CRED=$4
  log.info "Started ${SCRIPT_LOG_MESSAGE} in ${COL_PURPLE}${SERVER}:${PORT}"
  sendRequestToServer "${SERVER}" "${PORT}" "${IS_HTTPS}" "${USE_DOCKER_DEMO_CRED}" &
  wait
  log.info "${COL_RED}Finished ${SCRIPT_LOG_MESSAGE} in ${COL_CYAN}${SERVER}:${PORT}"
}

sendRequestToServer() {
  local SERVER=$1
  local PORT=$2
  local IS_HTTPS=$3
  local USE_DOCKER_DEMO_CRED=$4
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
  log.trace "${PROTOCOL}://${SERVER}:${PORT} response: '${RESPONSE}'"
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -a)
        SCRIPT_ARGS="${CURRENT_OPTION_ARG}"
        ;;
      -s)
        SCRIPT="${CURRENT_OPTION_ARG}"
        ;;  
      # I can't use parseInvalidArgument here because the script arg might start with "-"     
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
