#!/bin/bash
# This script runs while building the dockerfile

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

KAMEHOUSE_USER=""

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  parseArguments "$@"
  configureSudoers
  installGroot
  configureMariadb
  startMariadb
  setupKameHouseMariadb
}

configureSudoers() {
  log.info "Setting up sudoers"
  /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/deploy/set-kamehouse-sudoers-permissions.sh -u ${KAMEHOUSE_USER}
}

installGroot() {
  log.info "Installing groot"
  /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/groot/install-kamehouse-groot.sh -u ${KAMEHOUSE_USER} 
}

configureMariadb() {
  log.info "Setting up mariadb"
  # Open mariadb to external connections
  sed -i "s#bind-address            = 127.0.0.1#bind-address            = 0.0.0.0#g" /etc/mysql/mariadb.conf.d/50-server.cnf 
}

startMariadb() {
  log.info "Starting mariadb"
  service mariadb start 
  sleep 5 
  service mariadb start
  sleep 5 
  service mariadb start 
  sleep 5 
  service mariadb start 
  sleep 5 
  service mariadb start 
}

setupKameHouseMariadb() {
  log.info "Setting up kamehouse mariadb initial dump"
  mariadb -e"set @kameHousePass = '`cat /home/${KAMEHOUSE_USER}/docker/keys/.kamehouse-secrets.cfg | grep MARIADB_PASS_KAMEHOUSE | cut -d '=' -f 2`'; `cat /home/${KAMEHOUSE_USER}/git/kamehouse/kamehouse-shell/sql/mariadb/add-kamehouse-user.sql`" 
  mariadb < /home/${KAMEHOUSE_USER}/git/kamehouse/kamehouse-shell/sql/mariadb/create-kamehouse-schema.sql 
  mariadb kamehouse < /home/${KAMEHOUSE_USER}/git/kamehouse/kamehouse-shell/sql/mariadb/spring-session.sql
  mariadb kamehouse < /home/${KAMEHOUSE_USER}/git/kamehouse/kamehouse-shell/sql/mariadb/dump-kamehouse.sql
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
      -u)
        KAMEHOUSE_USER="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        log.error "Invalid argument ${CURRENT_OPTION}"
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
  echo -e "Usage: ${COL_PURPLE}dockerfile-root-setup-kamehouse.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-u (username)${COL_NORMAL} user running kamehouse [${COL_RED}required${COL_NORMAL}]"
}

main "$@"
