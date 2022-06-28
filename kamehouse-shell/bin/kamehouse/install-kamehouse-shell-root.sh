#!/bin/bash

COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_MESSAGE=${COL_GREEN}

KAMEHOUSE_USER=""

main() {
  parseArguments "$@"
  log.info "Setting up root user for kamehouse"
  if (( $EUID != 0 )); then
    log.error "This script needs to run as root. Exiting..."
    exit 1
  fi
  cd /home/${KAMEHOUSE_USER}/git/kamehouse ; ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
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
  while getopts ":hu:" OPT; do
    case $OPT in
    ("h")
      printHelp
      exit 0
      ;;
    ("u")
      KAMEHOUSE_USER=$OPTARG
      ;;
    (\?)
      log.error "Invalid argument $OPTARG"
      exit 1
      ;;
    esac
  done

  if [ -z "${KAMEHOUSE_USER}" ]; then
    log.error "Option -u is required"
    printHelp
    exit 1
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}install-kamehouse-shell-root.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-u (username)${COL_NORMAL} user running kamehouse [${COL_RED}required${COL_NORMAL}]"
}

main "$@"
