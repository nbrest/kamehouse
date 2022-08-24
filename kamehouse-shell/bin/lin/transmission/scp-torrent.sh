#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=true
FILE_ARG=""
SCP_SERVER="niko-server"
SOURCE_PATH="${HOME}/torrents/transmission/downloads/"
DEST_PATH="N:\\Z-DOWNLOADS\\torrents"

mainProcess() {
  local FIRST_CHAR="${FILE_ARG:0:1}"
  if [[ "${FIRST_CHAR}" == " " ]]; then
    FILE_ARG=${FILE_ARG:1}
  fi
  log.info "Transfering ${FILE_ARG} to ${SCP_SERVER}"
  log.debug "scp -r -C -v \"${SOURCE_PATH}${FILE_ARG}\" ${SCP_SERVER}:${DEST_PATH}"
  scp -r -C -v "${SOURCE_PATH}${FILE_ARG}" ${SCP_SERVER}:${DEST_PATH}
}

parseArguments() {
  while getopts ":f:" OPT; do
    case $OPT in
    ("f")
      FILE_ARG=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  checkRequiredOption "-f" "${FILE_ARG}"
}

printHelpOptions() {
  addHelpOption "-f (file)" "file or folder to transfer to ${SCP_SERVER}" "r"
}

main "$@"
