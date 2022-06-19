#!/bin/bash

# Execute this script with: 
# chmod a+x uninstall-kamehouse.sh ; ./uninstall-kamehouse.sh

COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_MESSAGE=${COL_GREEN}

PURGE_CONFIG=false

main() {
  parseArguments "$@"
  log.info "Uninstalling ${COL_PURPLE}kamehouse"

  revertBashRc
  deleteTomcatWebapps
  deleteKameHouseShell
  deleteKameHouseCmd
  deleteKameHouseGit

  if ${PURGE_CONFIG}; then
    purgeConfigFiles
  fi

  log.info "This script doesn't remove the database contents. To do that, login to mysql and execute 'DROP SCHEMA IF EXISTS kameHouse;'"
  log.info "Finished uninstalling ${COL_PURPLE}kamehouse"
}

revertBashRc() {
  log.info "Reverting ${HOME}/.bashrc"
  sed -i "s#source \${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh##Ig" "${HOME}/.bashrc"
}

deleteTomcatWebapps() {
  log.info "Deleting tomcat webapps"
  rm -r -f ${HOME}/programs/apache-tomcat/webapps/kame-house*
}

deleteKameHouseShell() {
  log.info "Deleting kamehouse shell scripts"
  rm -r -f ${HOME}/programs/kamehouse-shell
}

deleteKameHouseCmd() {
  log.info "Deleting kamehouse cmd"
  rm -r -f ${HOME}/programs/kamehouse-cmd
}

deleteKameHouseGit() {
  log.info "Deleting kamehouse git repository"
  rm -r -f ${HOME}/git/kamehouse
}

purgeConfigFiles() {
  log.info "Deleting all config files"
  rm -r -f -v ${HOME}/.kamehouse/.shell
  rm -r -f -v ${HOME}/home-synced/.kamehouse/keys
  rm -f -v ${HOME}/home-synced/.kamehouse/integration-test-cred.enc
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
  while getopts ":hp" OPT; do
    case $OPT in
    ("h")
      printHelp
      exit 0
      ;;
    ("p")
      PURGE_CONFIG=true
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
  echo -e "Usage: ${COL_PURPLE}uninstall-kamehouse.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-p${COL_NORMAL} purge config files as well"
}

main "$@"
