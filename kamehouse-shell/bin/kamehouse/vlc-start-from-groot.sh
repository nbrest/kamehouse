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
loadKamehouseShellPwd

FILE_TO_PLAY=""

mainProcess() {
  startVlcFromGroot
}

startVlcFromGroot() {
  log.info "Sending groot request to start vlc to play the file: ${COL_PURPLE}${FILE_TO_PLAY}"
  local SCRIPT="kamehouse/vlc-start.sh"
  local SCRIPT_ARGS="-f \\\"${FILE_TO_PLAY}\\\""
  SCRIPT_ARGS="$(urlencode "${SCRIPT_ARGS}")"
  local URL="http://localhost/kame-house-groot/api/v1/admin/kamehouse-shell/execute.php?script=${SCRIPT}&args=${SCRIPT_ARGS}"
  log.info "Executing request to ${COL_PURPLE}${URL}"
  curl --max-time 12 -k --request GET "${URL}" --header "Authorization: Basic ${GROOT_API_BASIC_AUTH}"
}

parseArguments() {
  while getopts ":f:" OPT; do
    case $OPT in
    ("f")
      FILE_TO_PLAY="$OPTARG"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done 
}

setEnvFromArguments() {
  checkRequiredOption "-f" "${FILE_TO_PLAY}" 
}

printHelpOptions() {
  addHelpOption "-f file" "File to play" "r"
}

main "$@"
