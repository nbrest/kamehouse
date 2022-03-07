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

MODULE_SHORT=

mainProcess() {
  deployInAllServers
}

deployInAllServers() {
  # niko-server
  deployInServer "niko-server" "80" "win" &
  #deployInServer "niko-server" "7080" "lin" &
  # niko-server-vm-ubuntu
  deployInServer "niko-server-vm-ubuntu" "80" "lin" &
  deployInServer "niko-server-vm-ubuntu" "7080" "lin" &
  # pi
  deployInServer "pi" "80" "lin" &
  #deployInServer "pi" "7080" "lin" &
  # niko-nba
  deployInServer "niko-nba" "80" "win" &
  # niko-w
  deployInServer "niko-w" "80" "win" &
  # niko-w-vm-ubuntu
  deployInServer "niko-w-vm-ubuntu" "80" "lin" &

  log.info "Waiting for deployment to finish in all servers. ${COL_YELLOW}This process can take several minutes"
  wait
  log.info "${COL_RED}Finished deploying in all servers"
}

deployInServer() {
  local SERVER=$1
  local PORT=$2
  local HOST_OS=$3
  log.info "Started deployInServer ${COL_PURPLE}${SERVER}:${PORT}:${HOST_OS}"
  gitPullAll ${SERVER} ${PORT} ${HOST_OS} &
  deployKamehouse ${SERVER} ${PORT} &
  wait
  log.info "${COL_RED}Finished deployInServer ${COL_CYAN}${SERVER}:${PORT}:${HOST_OS}"
}

gitPullAll() {
  local SERVER=$1
  local PORT=$2
  local HOST_OS=$3
  log.info "Started gitPullAll ${COL_PURPLE}${SERVER}:${PORT}:${HOST_OS}"
  executeScriptInServer ${SERVER} ${PORT} "${HOST_OS}/git/git-pull-all.sh"
  log.info "Finished gitPullAll ${COL_PURPLE}${SERVER}:${PORT}:${HOST_OS}"
}

deployKamehouse() {
  local SERVER=$1
  local PORT=$2
  local SCRIPT_ARGS="-f"
  if [ -n "${MODULE_SHORT}" ]; then
    SCRIPT_ARGS="${SCRIPT_ARGS} -m ${MODULE_SHORT}"
  fi
  log.info "Started deployKamehouse ${COL_PURPLE}${SERVER}:${PORT}"
  executeScriptInServer ${SERVER} ${PORT} "kamehouse/deploy-java-web-kamehouse.sh" "${SCRIPT_ARGS}"
  log.info "Finished deployKamehouse ${COL_PURPLE}${SERVER}:${PORT}"
}

executeScriptInServer() {
  local SERVER=$1
  local PORT=$2
  local SCRIPT=$3
  local SCRIPT_ARGS="$4"
  local URL=
  local URL_ENCODED_PARAMS=
  
  if [ -n "${SCRIPT_ARGS}" ]; then 
    URL_ENCODED_PARAMS="script=${SCRIPT}&args="$(urlencode "${SCRIPT_ARGS}")
  else
    URL_ENCODED_PARAMS="script=${SCRIPT}"
  fi
  URL="http://${SERVER}:${PORT}/kame-house-groot/api/v1/admin/my-scripts/exec-script.php?${URL_ENCODED_PARAMS}"
  log.debug "Executing request: ${COL_BLUE}${URL}"
  RESPONSE=`curl --max-time 1800 -k --location --request GET "${URL}" --header "Authorization: Basic ${GROOT_API_BASIC_AUTH}" 2>/dev/null`
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
  while getopts ":hm:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
    ("m")
      MODULE_SHORT="$OPTARG"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-m (admin|cmd|groot|media|mobile|shell|tennisworld|testmodule|ui|vlcrc)${COL_NORMAL} module to build"
}

main "$@"
