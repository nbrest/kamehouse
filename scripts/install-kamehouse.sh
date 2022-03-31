#!/bin/bash

# Execute from the root of the kamehouse git project:
# chmod a+x ./scripts/install-kamehouse.sh
# ./scripts/install-kamehouse.sh

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
  logStep "Installing ${COL_PURPLE}kamehouse"
  checkPath
  installKameHouseShell
}

checkPath() {
  if [ ! -d "./kamehouse-shell/bin" ] || [ ! -d "./.git" ]; then
    logError "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exit 1
  fi
}

installKameHouseShell() {
  chmod a+x kamehouse-shell/bin/kamehouse/kamehouse-shell-install.sh
  ./kamehouse-shell/bin/kamehouse/kamehouse-shell-install.sh
}

logStep() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

logError() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_RED}ERROR${COL_NORMAL}] - ${COL_RED}${LOG_MESSAGE}${COL_NORMAL}"
}

parseArguments() {
  while getopts ":hp" OPT; do
    case $OPT in
    ("h")
      printHelp
      exit 0
      ;;
    (\?)
      logError "Invalid argument $OPTARG"
      exit 1
      ;;
    esac
  done
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}install-kamehouse.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
}

main "$@"
