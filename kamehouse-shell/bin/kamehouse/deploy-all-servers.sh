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

MODULE_SHORT=

mainProcess() {
  deployInAllServers
}

deployInAllServers() {
  # niko-server
  deployInServer "niko-server" "80" "win" "false" &

  # niko-server-vm-ubuntu
  deployInServer "niko-server-vm-ubuntu" "80" "lin" "false" &
  deployInServer "niko-server-vm-ubuntu" "12080" "lin" "true" &

  # pi
  deployInServer "pi" "80" "lin" "false" &
  
  # niko-nba
  deployInServer "niko-nba" "80" "win" "false" &

  # niko-w
  deployInServer "niko-w" "80" "win" "false" &

  # niko-w-vm-ubuntu
  deployInServer "niko-w-vm-ubuntu" "80" "lin" "false" &

  log.info "Waiting for deployment to finish in all servers. ${COL_YELLOW}This process can take several minutes"
  wait
  log.info "${COL_RED}Finished deploying in all servers"
}

deployInServer() {
  local SERVER=$1
  local PORT=$2
  local HOST_OS=$3
  local IS_DOCKER_DEMO=$4
  log.info "Started deployInServer ${COL_PURPLE}${SERVER}:${PORT}:${HOST_OS}"
  gitPullAll ${SERVER} ${PORT} ${HOST_OS} ${IS_DOCKER_DEMO} &
  deployKamehouse ${SERVER} ${PORT} ${IS_DOCKER_DEMO} &
  wait
  log.info "${COL_RED}Finished deployInServer ${COL_CYAN}${SERVER}:${PORT}:${HOST_OS}"
}

gitPullAll() {
  local SERVER=$1
  local PORT=$2
  local HOST_OS=$3
  local IS_DOCKER_DEMO=$4
  log.info "Started gitPullAll ${COL_PURPLE}${SERVER}:${PORT}:${HOST_OS}"
  executeScriptInServer ${SERVER} ${PORT} ${IS_DOCKER_DEMO} "${HOST_OS}/git/git-pull-all.sh" 
  log.info "Finished gitPullAll ${COL_PURPLE}${SERVER}:${PORT}:${HOST_OS}"
}

deployKamehouse() {
  local SERVER=$1
  local PORT=$2
  local IS_DOCKER_DEMO=$3
  local SCRIPT_ARGS=""
  if [ -n "${MODULE_SHORT}" ]; then
    SCRIPT_ARGS="${SCRIPT_ARGS} -m ${MODULE_SHORT}"
  fi
  log.info "Started deployKamehouse ${COL_PURPLE}${SERVER}:${PORT}"
  executeScriptInServer ${SERVER} ${PORT} ${IS_DOCKER_DEMO} "kamehouse/deploy-kamehouse.sh" "${SCRIPT_ARGS}"
  log.info "Finished deployKamehouse ${COL_PURPLE}${SERVER}:${PORT}"
}

executeScriptInServer() {
  local SERVER=$1
  local PORT=$2
  local IS_DOCKER_DEMO=$3
  local SCRIPT=$4
  local SCRIPT_ARGS="$5"
  local URL=""
  local URL_ENCODED_PARAMS=""
  local BASIC_AUTH=""

  if [ -n "${SCRIPT_ARGS}" ]; then 
    URL_ENCODED_PARAMS="script=${SCRIPT}&args="$(urlencode "${SCRIPT_ARGS}")
  else
    URL_ENCODED_PARAMS="script=${SCRIPT}"
  fi

  if ${IS_DOCKER_DEMO}; then
    BASIC_AUTH=${DOCKER_DEMO_GROOT_API_BASIC_AUTH}
  else
    BASIC_AUTH=${GROOT_API_BASIC_AUTH}
  fi

  URL="http://${SERVER}:${PORT}/kame-house-groot/api/v1/admin/kamehouse-shell/exec-script.php?${URL_ENCODED_PARAMS}"
  log.info "Executing request: ${COL_BLUE}${URL}"
  RESPONSE=`curl --max-time 1800 -k --location --request GET "${URL}" --header "Authorization: Basic ${BASIC_AUTH}" 2>/dev/null`
  #echo "${RESPONSE}"
}

urlencode() {
  # urlencode <string>
  old_lc_collate=$LC_COLLATE
  LC_COLLATE=C
  local length="${#1}"
  for (( i = 0; i < length; i++ )); do
      local c="${1:$i:1}"
      case $c in
          [a-zA-Z0-9.~_-]) printf '%s' "$c" ;;
          *) printf '%%%02X' "'$c" ;;
      esac
  done
  LC_COLLATE=$old_lc_collate
}

parseArguments() {
  while getopts ":m:" OPT; do
    case $OPT in
    ("m")
      MODULE_SHORT="$OPTARG"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

printHelpOptions() {
  addHelpOption "-m ${MODULES_LIST}" "module to build"
}

main "$@"
