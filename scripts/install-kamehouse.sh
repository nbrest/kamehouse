#!/bin/bash

# Execute this script with: 
# chmod a+x install-kamehouse.sh ; ./install-kamehouse.sh

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

KAMEHOUSE_SHELL_ONLY=false
KAMEHOUSE_SHELL_SCRIPTS_ONLY=false

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  parseArguments "$@"
  log.info "Installing ${COL_PURPLE}kamehouse"
  gitCloneKameHouse
  checkPath
  installKameHouseShell
  if ${KAMEHOUSE_SHELL_ONLY}; then
    logInstallRootMessage
    log.info "Finished installing ${COL_PURPLE}kamehouse-shell${COL_MESSAGE} standalone. Running with -s so skipping the rest"
    exit ${EXIT_SUCCESS}
  fi
  buildKameHouseConfigDir
  setSudoersPermissions
  installKameHouseGroot
  deployKameHouse
  logInstallRootMessage
  log.info "Finished installing ${COL_PURPLE}kamehouse"
}

gitCloneKameHouse() {
  log.info "Cloning kamehouse git repository into ${HOME}/git/kamehouse"
  mkdir -p ${HOME}/git
  cd ${HOME}/git

  if [ ! -d "./kamehouse" ]; then
    git clone https://github.com/nbrest/kamehouse.git
  else
    log.info "kamehouse repository already exists"
  fi

  cd kamehouse
  git checkout dev
  git pull origin dev
}

checkPath() {
  if [ ! -d "./kamehouse-shell/bin" ] || [ ! -d "./.git" ]; then
    log.error "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exit ${EXIT_ERROR}
  fi
}

installKameHouseShell() {
  chmod a+x kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
  if ${KAMEHOUSE_SHELL_SCRIPTS_ONLY}; then
    ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh -o
  else
    ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
  fi  
}

buildKameHouseConfigDir() {
  log.info "Building kamehouse config dirs"
  mkdir -p ${HOME}/logs
  mkdir -p ${HOME}/.kamehouse/config/keys

  if [ ! -f "${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg" ]; then
    log.info ".kamehouse/config/keys/.kamehouse-secrets.cfg file doesn't exist, creating one from the sample"
    cp -v docker/keys/.kamehouse-secrets.cfg ${HOME}/.kamehouse/config/keys/
  else
    log.info ".kamehouse/config/keys/.kamehouse-secrets.cfg file already exists. skipping"
  fi

  if [ ! -f "${HOME}/.kamehouse/config/keys/.unlock.screen.pwd.enc" ]; then
    log.info ".kamehouse/config/keys/.unlock.screen.pwd.enc file doesn't exist, creating one from the sample"
    cp -v docker/keys/.unlock.screen.pwd.enc ${HOME}/.kamehouse/config/keys/
  else
    log.info ".kamehouse/config/keys/.unlock.screen.pwd.enc file already exists. skipping"
  fi

  if [ ! -f "${HOME}/.kamehouse/config/keys/.vnc.server.pwd.enc" ]; then
    log.info ".kamehouse/config/keys/.vnc.server.pwd.enc file doesn't exist, creating one from the sample"
    cp -v docker/keys/.vnc.server.pwd.enc ${HOME}/.kamehouse/config/keys/
  else
    log.info ".kamehouse/config/keys/.vnc.server.pwd.enc file already exists. skipping"
  fi

  if [ ! -f "${HOME}/.kamehouse/config/keys/integration-test-cred.enc" ]; then
    log.info ".kamehouse/config/keys/integration-test-cred.enc file doesn't exist, creating one from the sample"
    cp -v docker/keys/integration-test-cred.enc ${HOME}/.kamehouse/config/keys/
  else
    log.info ".kamehouse/config/keys/integration-test-cred.enc file already exists. skipping"
  fi  

  if [ ! -f "${HOME}/.kamehouse/config/keys/kamehouse.pkcs12" ]; then
    log.info ".kamehouse/config/keys/kamehouse.pkcs12 file doesn't exist, creating one from the sample"
    cp -v kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 ${HOME}/.kamehouse/config/keys/kamehouse.pkcs12
  else
    log.info ".kamehouse/config/keys/kamehouse.pkcs12 file already exists. skipping"
  fi  

  if [ ! -f "${HOME}/.kamehouse/config/keys/kamehouse.crt" ]; then
    log.info ".kamehouse/config/keys/kamehouse.crt file doesn't exist, creating one from the sample"
    cp -v kamehouse-commons-core/src/test/resources/commons/keys/sample.crt ${HOME}/.kamehouse/config/keys/kamehouse.crt
  else
    log.info ".kamehouse/config/keys/kamehouse.crt file already exists. skipping"
  fi
  
  chmod -R 700 ${HOME}/.kamehouse/config
}

setSudoersPermissions() {
  log.info "Setting sudoers permissions"
  local KAMEHOUSE_USER=`whoami`
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-kamehouse-sudoers-permissions.sh -u ${KAMEHOUSE_USER}
}

installKameHouseGroot() {
  log.info "Installing ${COL_PURPLE}kamehouse-groot${COL_MESSAGE}"
  local KAMEHOUSE_USER=`whoami`
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/install-kamehouse-groot.sh -u ${KAMEHOUSE_USER}
}

deployKameHouse() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/deploy-kamehouse.sh
}

logInstallRootMessage() {
  log.info "${COL_YELLOW}OPTIONAL:${COL_MESSAGE} If running on ${COL_PURPLE}linux${COL_MESSAGE}, setup ${COL_PURPLE}root${COL_MESSAGE} account to use kamehouse-shell as well by running the script ${COL_PURPLE}\${HOME}/programs/kamehouse-shell/bin/kamehouse/install-kamehouse-shell-root.sh as ${COL_PURPLE}root${COL_MESSAGE}"
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
      -o)
        KAMEHOUSE_SHELL_ONLY=true
        KAMEHOUSE_SHELL_SCRIPTS_ONLY=true
        ;;
      -s)
        KAMEHOUSE_SHELL_ONLY=true
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
  echo -e "Usage: ${COL_PURPLE}install-kamehouse.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-o${COL_NORMAL} only install kamehouse-shell scripts. Don't modify the shell"
  echo -e "     ${COL_BLUE}-s${COL_NORMAL} install kamehouse-shell, incluiding the changes to the shell"
}

main "$@"
