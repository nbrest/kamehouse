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
  log.info "Started setting sudoers for kamehouse"
  updateSudoers
  log.info "Ignore ${COL_PURPLE}sudo${COL_MESSAGE} error on windows. This is only needed in linux"
  log.info "Done setting sudoers for kamehouse"
}

updateSudoers() {
  log.info "Adding ${KAMEHOUSE_USER} user to adm and sudo groups"
  # adm: to be able to tail apache httpd logs
  sudo usermod -a -G adm ${KAMEHOUSE_USER}
  sudo usermod -a -G sudo ${KAMEHOUSE_USER}
  log.info "Updating sudoers file to run kamehouse"
  updateSudoersEntry "www-data ALL=(ALL) NOPASSWD: /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/common/sudoers/www-data/su.sh"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(ALL) NOPASSWD: /usr/bin/mariadb"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(ALL) NOPASSWD: /usr/bin/netstat"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(ALL) NOPASSWD: /usr/bin/systemctl"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(ALL) NOPASSWD: /usr/sbin/reboot"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(ALL) NOPASSWD: /usr/sbin/service"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(ALL) NOPASSWD: /usr/sbin/shutdown"
}

updateSudoersEntry() {
  local SUDOERS_LINE="$1"
  sudo cat /etc/sudoers | grep "${SUDOERS_LINE}" > /dev/null
  if [ "$?" != "0" ]; then
    log.info "${SUDOERS_LINE} NOT in sudoers file. Adding it"
    sudo /bin/bash -c "echo \"\" >> /etc/sudoers"
    sudo /bin/bash -c "echo \"${SUDOERS_LINE}\" >> /etc/sudoers"
  else 
    log.info "'${SUDOERS_LINE}' is already in sudoers. No need to update"
  fi    
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
  echo -e "Usage: ${COL_PURPLE}set-kamehouse-sudoers-permissions.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-u (username)${COL_NORMAL} user running kamehouse [${COL_RED}required${COL_NORMAL}]"
}

main "$@"
