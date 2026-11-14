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
  buildKameHouseHomeFolderDir
  installKameHouseShell
  if ${KAMEHOUSE_SHELL_ONLY}; then
    log.info "Finished installing ${COL_PURPLE}kamehouse-shell${COL_MESSAGE} standalone. Running with -s so skipping the rest"
    exit ${EXIT_SUCCESS}
  fi
  setupKameHouseSecrets
  setSudoersPermissions
  deployKameHouse
  log.info "Finished installing ${COL_PURPLE}kamehouse"
}

gitCloneKameHouse() {
  log.info "Cloning kamehouse git repository into ${HOME}/.kamehouse/git/kamehouse"
  mkdir -p ${HOME}/.kamehouse/git
  cd ${HOME}/.kamehouse/git

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

buildKameHouseHomeFolderDir() {
  log.info "Building kamehouse home folder dirs"
  mkdir -p ${HOME}/.kamehouse/.vscode
  mkdir -p ${HOME}/.kamehouse/backup/db
  mkdir -p ${HOME}/.kamehouse/backup/docker
  mkdir -p ${HOME}/.kamehouse/backup/local  
  mkdir -p ${HOME}/.kamehouse/conf
  mkdir -p ${HOME}/.kamehouse/data
  mkdir -p ${HOME}/.kamehouse/opt
  mkdir -p ${HOME}/.kamehouse/secrets
  mkdir -p ${HOME}/.kamehouse/var/log
  mkdir -p ${HOME}/.kamehouse/var/tmp
  mkdir -p ${HOME}/.kamehouse/var/www/html

  cp -vr local-setup/home-kamehouse/vscode ${HOME}/.kamehouse/.vscode 
}

setupKameHouseSecrets() {
  if [ ! -f "${HOME}/.kamehouse/secrets/kamehouse.pkcs12" ]; then
    log.info ".kamehouse/secrets/kamehouse.pkcs12 file doesn't exist, creating one from the sample"
    cp -v kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 ${HOME}/.kamehouse/secrets/kamehouse.pkcs12
  else
    log.info ".kamehouse/secrets/kamehouse.pkcs12 file already exists. skipping"
  fi  

  if [ ! -f "${HOME}/.kamehouse/secrets/kamehouse.crt" ]; then
    log.info ".kamehouse/secrets/kamehouse.crt file doesn't exist, creating one from the sample"
    cp -v kamehouse-commons-core/src/test/resources/commons/keys/sample.crt ${HOME}/.kamehouse/secrets/kamehouse.crt
  else
    log.info ".kamehouse/secrets/kamehouse.crt file already exists. skipping"
  fi
  
  if [ ! -f "${HOME}/.kamehouse/secrets/.kamehouse-secrets.cfg.enc" ]; then
    log.info ".kamehouse/secrets/.kamehouse-secrets.cfg.enc file doesn't exist, creating one from the sample"
    cp -v docker/setup-kamehouse/secrets/.kamehouse-secrets.cfg.enc ${HOME}/.kamehouse/secrets/
    cp -v kamehouse-commons-core/src/test/resources/commons/keys/sample.key ${HOME}/.kamehouse/secrets/kamehouse.key
    cp -v kamehouse-commons-core/src/test/resources/commons/keys/sample.pub ${HOME}/.kamehouse/secrets/kamehouse.pub
    cp -v kamehouse-commons-core/src/test/resources/commons/keys/secrets.key.enc ${HOME}/.kamehouse/secrets/kamehouse-secrets.key.enc
  else
    log.info ".kamehouse/secrets/.kamehouse-secrets.cfg.enc file already exists. skipping"
  fi 

  if [ -f "${HOME}/.kamehouse/secrets/.kamehouse-secrets.cfg" ]; then
    log.info "WARNING!!!!!!!!!!!!!!!! Found decrypted ${HOME}/.kamehouse/secrets/.kamehouse-secrets.cfg file. Encrypt it with encrypt-kamehouse-secrets.sh"
  fi 
  chmod -R 700 ${HOME}/.kamehouse/secrets
}

installKameHouseShell() {
  chmod a+x kamehouse-shell/bin/shell/install-kamehouse-shell.sh
  if ${KAMEHOUSE_SHELL_SCRIPTS_ONLY}; then
    ./kamehouse-shell/bin/shell/install-kamehouse-shell.sh -o
  else
    ./kamehouse-shell/bin/shell/install-kamehouse-shell.sh
  fi  
}

setSudoersPermissions() {
  log.info "Setting sudoers permissions"
  local KAMEHOUSE_USER=`whoami`
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/deploy/set-kamehouse-sudoers-permissions.sh -u ${KAMEHOUSE_USER}
}

deployKameHouse() {
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/deploy/deploy-kamehouse.sh
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
