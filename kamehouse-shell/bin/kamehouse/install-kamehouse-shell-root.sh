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

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  parseCmdLineArguments "$@"
  log.info "Setting up root user for kamehouse"
  gitCloneKameHouse
  sudo /bin/bash -c 'cd /root/git/kamehouse ; chmod a+x kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh'
  sudo /bin/bash -c 'cd /root/git/kamehouse ; ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh'
  log.info "To ${COL_RED}uninstall${COL_MESSAGE} kamehouse-shell for root, run as root ${COL_PURPLE}cd /root/git/kamehouse ; ./scripts/uninstall-kamehouse.sh"
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

gitCloneKameHouse() {
  log.info "Cloning kamehouse git repository into /root/git/kamehouse"
  sudo mkdir -p /root/git
  sudo /bin/bash -c 'cd /root/git ; git clone https://github.com/nbrest/kamehouse.git ; cd kamehouse ; git checkout dev ; git pull origin dev'
}

parseCmdLineArguments() {
  while getopts ":h" OPT; do
    case $OPT in
    ("h")
      printHelpMenu
      exit ${EXIT_SUCCESS}
      ;;
    (\?)
      log.error "Invalid argument $OPTARG"
      exit ${EXIT_INVALID_ARG}
      ;;
    esac
  done
}

printHelpMenu() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}install-kamehouse-shell-root.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
}

main "$@"
