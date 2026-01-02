#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/kamehouse/docker-functions.sh

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  SCRIPT=""
  SCRIPT_ARGS=""
  SCRIPT_LOG_MESSAGE=""
}

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

  local EXEC_CURL_OUTPUT=`execCurlRequest "${URL}" "${BASIC_AUTH}" 2>&1`
  log.debug "${EXEC_CURL_OUTPUT}" --log-message-only
}

execCurlRequest() {
  local URL=$1
  local BASIC_AUTH=$2
  local SESSION_ID=$RANDOM
  log.debug "SID:${SESSION_ID}: Executing request: ${COL_BLUE}${URL}"
  local CURL_RESPONSE=`curl --max-time 1800 -k --location --request GET "${URL}" --header "Authorization: Basic ${BASIC_AUTH}" 2>/dev/null`
  log.debug "${COL_CYAN}---------- ${URL} response start. SID:${SESSION_ID}"
  log.debug "${CURL_RESPONSE}" --log-message-only
  log.debug "${COL_CYAN}---------- ${URL} response end. SID:${SESSION_ID}"
}

parseArguments() {
  local OPTIONS=("$@")
  SCRIPT="${OPTIONS[1]}"

  OPTIONS=("${OPTIONS[@]:3}")
  SCRIPT_ARGS="${OPTIONS[@]}"
}

setEnvFromArguments() {
  checkRequiredOption "-s" "${SCRIPT}"
}

printHelpOptions() {
  addHelpOption "-s (script)" "script to execute" "r"
  addHelpOption "-a (args)" "script args"
}

printHelpFooter() {
  echo -e ""
  echo -e "${COL_YELLOW}   > IMPORTANT: The order the of arguments is important! -s must be set before -a${COL_NORMAL}"
  echo -e ""  
}

main "$@"
