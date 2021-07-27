#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
SCRIPT=""
SCRIPT_ARGS=""
BASE_PATH="${HOME}/my.scripts/"

mainProcess() {
  log.info "Executing script ${BASE_PATH}${SCRIPT} with arguments ${SCRIPT_ARGS}"
  ${BASE_PATH}${SCRIPT} ${SCRIPT_ARGS}
}

parseArguments() {
  while getopts ":hs:a:" OPT; do
    case $OPT in
    ("a")
      SCRIPT_ARGS=$OPTARG
      ;;
    ("h")
      printHelp
      exitProcess 0
      ;;
    ("s")
      SCRIPT=$OPTARG
      ;;
    (\?)
      log.error "Invalid option: -$OPTARG"
      printHelp
      exitProcess 1
      ;;
    esac
  done

  if [ -z "${SCRIPT}" ]; then
    log.error "Option -s script is required"
    printHelp
    exitProcess 1
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"
  echo -e "     ${COL_BLUE}-s (script)${COL_NORMAL} script to execute"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-s (script)${COL_NORMAL} script to execute [${COL_RED}required${COL_NORMAL}]"
}

main "$@"
