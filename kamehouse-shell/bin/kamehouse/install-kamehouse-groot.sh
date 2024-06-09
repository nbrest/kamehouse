#!/bin/bash

SCRIPT_NAME=`basename "$0"`
COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_PURPLE_STD="\033[0;35m"
COL_MESSAGE=${COL_GREEN}

KAMEHOUSE_USER=""

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  parseCmdLineArguments "$@"
  log.info "Installing ${COL_PURPLE}kamehouse-groot${COL_MESSAGE}"
  addKameHouseUserPropertiesFile
  log.info "Ignore ${COL_PURPLE}sudo${COL_MESSAGE} error on windows. This is only needed in linux"
  log.info "Done installing ${COL_PURPLE}kamehouse-groot!"
}

addKameHouseUserPropertiesFile() {
  local KAMEHOUSE_USER_FILE=/var/www/.kamehouse-user
  log.info "Adding file ${KAMEHOUSE_USER_FILE}"
  sudo /bin/bash -c "echo \"# File auto generated by install-kamehouse-groot.sh. Don't update manually\" > ${KAMEHOUSE_USER_FILE}"
  sudo /bin/bash -c "echo \"KAMEHOUSE_USER=${KAMEHOUSE_USER}\" >> ${KAMEHOUSE_USER_FILE}"
  sudo chown www-data:www-data ${KAMEHOUSE_USER_FILE}
}

log.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_PURPLE_STD}${SCRIPT_NAME}${COL_NORMAL} - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

log.error() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_RED}ERROR${COL_NORMAL}] - ${COL_RED}${SCRIPT_NAME}${COL_NORMAL} - ${COL_RED}${LOG_MESSAGE}${COL_NORMAL}"
}

parseCmdLineArguments() {
  while getopts ":hu:" OPT; do
    case $OPT in
    ("h")
      printHelpMenu
      exit ${EXIT_SUCCESS}
      ;;
    ("u")
      KAMEHOUSE_USER=$OPTARG
      ;;
    (\?)
      log.error "Invalid argument $OPTARG"
      exit ${EXIT_INVALID_ARG}
      ;;
    esac
  done

  if [ -z "${KAMEHOUSE_USER}" ]; then
    log.error "Option -u is required"
    printHelpMenu
    exit ${EXIT_INVALID_ARG}
  fi
}

printHelpMenu() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}install-kamehouse-groot.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-u (username)${COL_NORMAL} user running kamehouse [${COL_RED}required${COL_NORMAL}]"
}

main "$@"
