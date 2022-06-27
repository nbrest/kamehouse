#!/bin/bash

# Execute from the root of the kamehouse git project:
# chmod a+x ./kamehouse-shell/bin/kamehouse/install-kamehouse-groot.sh
# ./kamehouse-shell/bin/kamehouse/install-kamehouse-groot.sh

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
  log.info "Installing ${COL_PURPLE}kamehouse-groot${COL_MESSAGE}"
  log.info "User running this script needs ${COL_RED}sudo ln${COL_DEFAULT_LOG} permissions"
  createApacheHttpdUserSymLink
  log.info "Done installing ${COL_PURPLE}kamehouse-groot!"
}

createApacheHttpdUserSymLink() {
  local USERNAME=`whoami`
  log.info "Creating symlink on /var/www for apache user www-data to access kamehouse-shell scripts"
  log.info "This step is necessary to execute successfully kamehouse-shell commands from groot"
  log.info "This script is only necessary on ${COL_PURPLE}linux${COL_MESSAGE}. It's not needed in ${COL_PURPLE}windows" 
  sudo ln -s /home/${USERNAME}/programs /var/www
  log.info "Ignore ${COL_PURPLE}sudo${COL_MESSAGE} error on windows. Run with ${COL_PURPLE}sudo${COL_MESSAGE} permissions on linux"
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
  while getopts ":h" OPT; do
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
  echo -e "Usage: ${COL_PURPLE}install-kamehouse-groot.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
}

main "$@"
