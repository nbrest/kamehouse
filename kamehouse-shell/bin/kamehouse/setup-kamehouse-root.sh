#!/bin/bash

# Execute from the root of the kamehouse git project:
# chmod a+x ./kamehouse-shell/bin/kamehouse/setup-kamehouse-root.sh
# ./kamehouse-shell/bin/kamehouse/setup-kamehouse-root.sh

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
  log.warn "User running this script needs ${COL_RED}sudo bash,ln,mkdir${COL_DEFAULT_LOG} permissions"
  createLogsDir
  createRootSymLink
  FUNC=$(declare -f updateRootBashRc)
  sudo bash -c "$FUNC; updateRootBashRc"
  log.info "Finished setting up root user for kamehouse"
}

createLogsDir() {
  sudo mkdir -p /root/logs
}

createRootSymLink() {
  local USERNAME=`whoami`
  log.info "Creating symlink on root home" 
  sudo ln -s /home/${USERNAME}/programs /root/
  log.info "Ignore ${COL_PURPLE}sudo${COL_MESSAGE} error on windows, give ${COL_PURPLE}sudo${COL_MESSAGE} permissions to current user on linux if it fails on linux"
}

# Call this function with sudo to execute as root
updateRootBashRc() {
  INFO=" - [INFO] - "
  echo -e "$(date +%Y-%m-%d' '%H:%M:%S)${INFO}Updating /root/.bashrc"
  if [ ! -f "/root/.bashrc" ]; then
    echo -e "$(date +%Y-%m-%d' '%H:%M:%S)${INFO}/root/.bashrc not found. Creating one"
    echo "" > /root/.bashrc
    echo "source \${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh" >> /root/.bashrc
  else
    cat /root/.bashrc | grep "/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh" > /dev/null
    if [ "$?" != "0" ]; then
      echo -e "$(date +%Y-%m-%d' '%H:%M:%S)${INFO}Adding bashrc/bashrc.sh to /root/.bashrc"
      echo "" >> /root/.bashrc
      echo "source \${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh" >> /root/.bashrc
    else
      echo -e "$(date +%Y-%m-%d' '%H:%M:%S)${INFO}/root/.bashrc already sources \${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh. No need to update"
    fi
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
