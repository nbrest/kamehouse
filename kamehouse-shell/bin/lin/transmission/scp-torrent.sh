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
  scp -r -C -v "${SOURCE_PATH}${FILE_ARG}" ${SCP_SERVER}:${DEST_PATH}
}

parseArguments() {
  while getopts ":f:h" OPT; do
    case $OPT in
    ("f")
      FILE_ARG=$OPTARG
      ;;
    ("h")
      printHelp
      exitProcess 0
      ;;
    (\?)
      log.error "Invalid option: -$OPTARG"
      printHelp
      exitProcess 1
      ;;
    esac
  done

  if [ -z "${FILE_ARG}" ]; then
    log.error "Option -f file to transfer is required"
    printHelp
    exitProcess 1
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"
  echo -e "     ${COL_BLUE}-f (file)${COL_NORMAL} file or folder to transfer to ${SCP_SERVER} [${COL_RED}required${COL_NORMAL}]"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
}

main "$@"
