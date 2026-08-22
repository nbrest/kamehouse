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

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  parseArguments "$@"
  installBaseApps
}

installBaseApps() {
  log.info "Installing base apps"
  apt-get update -y && apt-get -y upgrade
  apt-get install -y apache2
  apt-get install -y curl
  apt-get install -y git
  apt-get install -y iputils-ping
  apt-get install -y openjdk-17-jdk
  apt-get install -y mariadb-server
  apt-get install -y net-tools
  apt-get install -y openssh-server
  apt-get install -y php libapache2-mod-php php-mysql
  apt-get install -y picom
  apt-get install -y python3
  apt-get install -y python3-pyqt5
  apt-get install -y python3-click
  apt-get install -y python3-google-api-python-client
  apt-get install -y python3-google-auth-httplib2
  apt-get install -y python3-google-auth-oauthlib
  apt-get install -y python3-loguru
  apt-get install -y python3-requests
  apt-get install -y python3-websocket
  apt-get install -y python3-websocket-client
  apt-get install -y python3-stomper
  apt-get install -y pip
  apt-get install -y screen
  apt-get install -y sudo
  apt-get install -y tightvncserver
  apt-get install -y tmux
  apt-get install -y vim
  apt-get install -y vlc
  apt-get install -y xcompmgr
  apt-get install -y zip

  apt-get autopurge -y
  apt-get autoclean -y
  apt-get clean -y

  installNode
  setupPython
}

setupPython() {
  if [ -f "/usr/bin/python" ]; then
    return
  fi

  if [ -f "/usr/bin/python3.10" ]; then
    rm -f /usr/bin/python
    ln -s /usr/bin/python3.10 /usr/bin/python
  fi

  if [ -f "/usr/bin/python3.11" ]; then
    rm -f /usr/bin/python
    ln -s /usr/bin/python3.11 /usr/bin/python
  fi

  if [ -f "/usr/bin/python3.12" ]; then
    rm -f /usr/bin/python
    ln -s /usr/bin/python3.12 /usr/bin/python
  fi

  if [ -f "/usr/bin/python3.13" ]; then
    rm -f /usr/bin/python
    ln -s /usr/bin/python3.13 /usr/bin/python
  fi

  if [ -f "/usr/bin/python3" ]; then
    rm -f /usr/bin/python
    ln -s /usr/bin/python3 /usr/bin/python
  fi
}

installNode() {
  log.info "Installing node"
  cd ~
  curl -sL https://deb.nodesource.com/setup_20.x | sudo bash - 
  
  sudo apt-get install nodejs -y && \
  apt-get autopurge -y && \
  apt-get autoclean -y && \
  apt-get clean -y

  npm install -g typescript
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
      -?|-??*)
        log.error "Invalid argument ${CURRENT_OPTION}"
        exit ${EXIT_INVALID_ARG}
        ;;
    esac
  done    
}

printHelpMenu() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
}

main "$@"
