#!/bin/bash

# Execute from the root of the kamehouse git project:
# chmod a+x ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell-root.sh
# ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell-root.sh

COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_MESSAGE=${COL_GREEN}

main() {
  parseArguments "$@"
  log.info "Setting up root user for kamehouse"
  log.info "Run this script as the user who installed and runs kamehouse"
  log.info "User running this script needs ${COL_RED}sudo su${COL_MESSAGE} permissions"
  sudo su -c "cd ${HOME}/git/kamehouse ; ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh"
  log.info "To ${COL_RED}uninstall${COL_MESSAGE} kamehouse-shell for root, run as root ${COL_PURPLE}cd ${HOME}/git/kamehouse ; ./scripts/uninstall-kamehouse.sh"
  log.info "Finished setting up root user for kamehouse"
}

log.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

log.error() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_RED}ERROR${COL_NORMAL}] - ${COL_RED}${LOG_MESSAGE}${COL_NORMAL}"
}

parseArguments() {
  while getopts ":hop" OPT; do
    case $OPT in
    ("h")
      printHelp
      exit 0
      ;;
    (\?)
      log.error "Invalid argument $OPTARG"
      exit 1
      ;;
    esac
  done
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}install-kamehouse-shell.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
}

main "$@"
