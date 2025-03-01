#!/bin/bash

# Execute this script with: 
# chmod a+x uninstall-kamehouse.sh ; ./uninstall-kamehouse.sh

SCRIPT_NAME=`basename "$0"`
COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_CYAN_STD="\033[0;36m"
COL_PURPLE_STD="\033[0;35m"
COL_MESSAGE=${COL_GREEN}

PURGE_CONFIG=false
KAMEHOUSE_SHELL_ONLY=false
UNINSTALL_FOR_ROOT=false

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  parseArguments "$@"
  checkUninstalForRoot

  log.info "Uninstalling ${COL_PURPLE}kamehouse"

  revertBashRc
  deleteKameHouseShell

  if ${KAMEHOUSE_SHELL_ONLY}; then
    log.info "Uninstalling only kamehouse-shell. Skipping the rest of the steps"
  else 
    deleteTomcatWebapps
    deleteKameHouseCmd
    deleteKameHouseUiStaticFiles
    deleteKameHouseGroot
    deleteKameHouseGit
    purgeConfigFiles
  
    log.info "This script doesn't remove the database contents. To do that, login to mariadb and execute 'DROP SCHEMA IF EXISTS kamehouse;'"
  fi

  log.info "Finished uninstalling ${COL_PURPLE}kamehouse"
}

checkUninstalForRoot() {
  if ${UNINSTALL_FOR_ROOT}; then
    sudo /bin/bash -c "cd /root/git/kamehouse ; ./scripts/uninstall-kamehouse.sh -p"
    log.info "Uninstalled kamehouse for root user"
    exit ${EXIT_SUCCESS}
  fi
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

deleteKameHouseUiStaticFiles() {
  log.info "Deleting kamehouse ui static files"
  if [ -d "/var/www/kamehouse-webserver" ]; then
    rm -rf /var/www/kamehouse-webserver/kame-house
  fi
  
  if [ -d "${HOME}/programs/apache-httpd/www/kamehouse-webserver" ]; then
    rm -rf ${HOME}/programs/apache-httpd/www/kamehouse-webserver/kame-house
  fi
}

deleteKameHouseGroot() {
  log.info "Deleting kamehouse groot"
  if [ -d "/var/www/kamehouse-webserver" ]; then
    rm -rf /var/www/kamehouse-webserver/kame-house-groot
  fi
  
  if [ -d "${HOME}/programs/apache-httpd/www/kamehouse-webserver" ]; then
    rm -rf ${HOME}/programs/apache-httpd/www/kamehouse-webserver/kame-house-groot
  fi

  if [ -f "/var/www/.kamehouse-server" ]; then
    rm -rf /var/www/.kamehouse-server
  fi  
}

deleteKameHouseGit() {
  log.info "Deleting kamehouse git repository"
  rm -r -f ${HOME}/git/kamehouse
}

purgeConfigFiles() {
  if ${PURGE_CONFIG}; then
    log.info "Deleting all config files"
    rm -r -f -v ${HOME}/.kamehouse/.shell/shell.pwd
    rm -r -f -v ${HOME}/.kamehouse/keys
  else
    log.info "Running without -p so skipping purging config files"
  fi
}

log.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_CYAN_STD}${SCRIPT_NAME}${COL_NORMAL} - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

log.error() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_RED}ERROR${COL_NORMAL}] - ${COL_RED}${SCRIPT_NAME}${COL_NORMAL} - ${COL_RED}${LOG_MESSAGE}${COL_NORMAL}"
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -h)
        printHelpMenu
        exit ${EXIT_SUCCESS}
        ;;
      -p)
        PURGE_CONFIG=true
        ;;
      -s)
        KAMEHOUSE_SHELL_ONLY=true
        ;;
      -r)
        UNINSTALL_FOR_ROOT=true
        ;;
      -?|-??*)
        log.error "Invalid argument ${CURRENT_OPTION}"
        exit ${EXIT_INVALID_ARG}
        ;;        
    esac
  done 
}

printHelpMenu() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}uninstall-kamehouse.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-p${COL_NORMAL} purge config files as well"
  echo -e "     ${COL_BLUE}-s${COL_NORMAL} uninstall kamehouse-shell only"
  echo -e "     ${COL_BLUE}-r${COL_NORMAL} uninstall kamehouse for root"
}

main "$@"
