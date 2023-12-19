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
  parseCmdLineArguments "$@"
  log.info "Installing ${COL_PURPLE}kamehouse-groot${COL_MESSAGE}"
  createApacheHttpdUserSymLink
  setWwwDataUserGroups
  setLogsDir
  setKameHouseUserHomePermissions
  log.info "Ignore ${COL_PURPLE}sudo${COL_MESSAGE} error on windows"
  log.info "Done installing ${COL_PURPLE}kamehouse-groot!"
}

createApacheHttpdUserSymLink() {
  log.info "Creating symlink on /var/www for apache user www-data to access kamehouse-shell scripts"
  log.info "This step is necessary to execute successfully kamehouse-shell commands from groot"
  log.info "This script is only necessary on ${COL_PURPLE}linux${COL_MESSAGE}. It's not needed in ${COL_PURPLE}windows" 
  sudo ln -s /home/${KAMEHOUSE_USER}/programs /var/www
  sudo ln -s /home/${KAMEHOUSE_USER}/.kamehouse /var/www
}

setLogsDir() {
  log.info "Setting logs dirs"
  sudo mkdir -p /var/www/logs
  sudo chmod a+rwx /var/www/logs
}

setKameHouseUserHomePermissions() {
  log.info "Setting /home/${KAMEHOUSE_USER} permissions"
  sudo chmod ug+rx /home/${KAMEHOUSE_USER}
  sudo chmod ug+rx /home/${KAMEHOUSE_USER}/.kamehouse
  sudo chown -R ${KAMEHOUSE_USER}:${KAMEHOUSE_USER} /home/${KAMEHOUSE_USER}/.kamehouse
  sudo chmod ug+rx /home/${KAMEHOUSE_USER}/programs
  sudo chmod o-rx /home/${KAMEHOUSE_USER}
  sudo chmod o-rx /home/${KAMEHOUSE_USER}/.kamehouse
  sudo chmod o-rx /home/${KAMEHOUSE_USER}/programs
}

setWwwDataUserGroups() {
  log.info "Adding www-data to ${KAMEHOUSE_USER} and users groups"
  sudo usermod -a -G ${KAMEHOUSE_USER} www-data
  sudo usermod -a -G users www-data
  log.info "www-data groups"
  sudo -u www-data groups
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

parseCmdLineArguments() {
  while getopts ":hu:" OPT; do
    case $OPT in
    ("h")
      printHelpMenu
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
    printHelpMenu
    exit 1
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
