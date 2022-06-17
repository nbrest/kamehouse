#!/bin/bash

# Execute from the root of the kamehouse git project:
# chmod a+x ./kamehouse-shell/bin/kamehouse/kamehouse-shell-install.sh
# ./kamehouse-shell/bin/kamehouse/kamehouse-shell-install.sh
DEFAULT_KAMEHOUSE_USERNAME=""

COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_MESSAGE=${COL_GREEN}

KAMEHOUSE_SHELL_PATH=${HOME}/programs/kamehouse-shell
TEMP_PATH=${HOME}/temp

KAMEHOUSE_SHELL_SOURCE="."

main() {
  parseArguments "$@"
  log.info "Installing ${COL_PURPLE}kamehouse-shell${COL_MESSAGE} to ${COL_PURPLE}${KAMEHOUSE_SHELL_PATH}"
  logScriptParameters
  checkSourcePath
  getDefaultKameHouseUsername
  installKameHouseShell
  fixPermissions
  createRootSymLink
  installCred
  updateUsername
  updateBashRc
  log.info "Done installing ${COL_PURPLE}kamehouse-shell!"
}

logScriptParameters() {
  log.info "KAMEHOUSE_SHELL_SOURCE: ${KAMEHOUSE_SHELL_SOURCE}"
}

checkSourcePath() {
  if [ ! -d "${KAMEHOUSE_SHELL_SOURCE}/kamehouse-shell/bin" ] || [ ! -d "${KAMEHOUSE_SHELL_SOURCE}/.git" ]; then
    log.error "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exit 1
  fi
}

getDefaultKameHouseUsername() {
  DEFAULT_KAMEHOUSE_USERNAME=`cat Dockerfile | grep "ARG KAMEHOUSE_USERNAME=" | awk -F'=' '{print $2}'`
  if [ -z "${DEFAULT_KAMEHOUSE_USERNAME}" ]; then
    log.error "Could not set default kamehouse username from Dockerfile"
    exit 1
  fi 
}

installKameHouseShell() {
  log.info "Rebuilding shell scripts directory"
  rm -r -f ${KAMEHOUSE_SHELL_PATH}
  mkdir -p ${KAMEHOUSE_SHELL_PATH}
  cp -r -f ${KAMEHOUSE_SHELL_SOURCE}/kamehouse-shell/bin ${KAMEHOUSE_SHELL_PATH}/
}

fixPermissions() {
  log.info "Fixing permissions"
  chmod -R a+x ${KAMEHOUSE_SHELL_PATH}
}

createRootSymLink() {
  local USERNAME=`whoami`
  log.info "Creating symlink on root home. Ignore the error on windows, give sudo permissions to current user on linux if it fails on linux"
  sudo ln -s /home/${USERNAME}/programs /root/
}

installCred() {
  log.info "Installing credentials file"
  if [ ! -f "${HOME}/.kamehouse/.shell/.cred" ]; then
    log.info "${COL_PURPLE}${HOME}/.kamehouse/.shell/.cred${COL_MESSAGE} not found. Creating it from template"
    mkdir -p ${HOME}/.kamehouse/.shell/
    cp docker/keys/.cred ${HOME}/.kamehouse/.shell/.cred
  fi
}

updateUsername() {
  local USERNAME=`whoami`
  log.info "Updating username in kamehouse-shell scripts to ${COL_PURPLE}${USERNAME}"
  sed -i "s#USERNAME=\"\${DEFAULT_KAMEHOUSE_USERNAME}\"#USERNAME=\"${USERNAME}\"#g" "${KAMEHOUSE_SHELL_PATH}/bin/kamehouse/get-username.sh"
  sed -i "s#USERHOME_LIN=\"/home/\${DEFAULT_KAMEHOUSE_USERNAME}\"#USERHOME_LIN=\"/home/${USERNAME}\"#g" "${KAMEHOUSE_SHELL_PATH}/bin/kamehouse/get-userhome.sh"
  sed -i "s#KAMEHOUSE_USER=\"\"#KAMEHOUSE_USER=\"${USERNAME}\"#g" "${KAMEHOUSE_SHELL_PATH}/bin/lin/startup/rc-local.sh"
  sed -i "s#KAMEHOUSE_USER=\"\"#KAMEHOUSE_USER=\"${USERNAME}\"#g" "${KAMEHOUSE_SHELL_PATH}/bin/pi/startup/rc-local.sh"
  sed -i "s#DEFAULT_KAMEHOUSE_USERNAME=\"\"#DEFAULT_KAMEHOUSE_USERNAME=\"${DEFAULT_KAMEHOUSE_USERNAME}\"#g" "${KAMEHOUSE_SHELL_PATH}/bin/common/kamehouse/kamehouse-functions.sh"
}

updateBashRc() {
  log.info "Updating ${COL_PURPLE}${HOME}/.bashrc"
  if [ ! -f "${HOME}/.bashrc" ]; then
    log.info "${COL_PURPLE}${HOME}/.bashrc${COL_MESSAGE} not found. Creating one"
    echo "" > ${HOME}/.bashrc
    echo "source \${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh" >> ${HOME}/.bashrc
  else 
    cat ${HOME}/.bashrc | grep "/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh" > /dev/null
    if [ "$?" != "0" ]; then
      log.info "Adding bashrc/bashrc.sh to ${COL_PURPLE}${HOME}/.bashrc"
      echo "" >> ${HOME}/.bashrc
      echo "source \${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh" >> ${HOME}/.bashrc
    else 
      log.info "${COL_PURPLE}${HOME}/.bashrc${COL_MESSAGE} already sources ${COL_PURPLE}${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh${COL_MESSAGE}. No need to update"
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
  while getopts ":hp" OPT; do
    case $OPT in
    ("h")
      printHelp
      exit 0
      ;;
    ("p")
      KAMEHOUSE_SHELL_SOURCE=${HOME}/git/kamehouse
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
  echo -e "Usage: ${COL_PURPLE}kamehouse-shell-install.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-p${COL_NORMAL} use kamehouse git prod directory instead of current dir"
}

main "$@"
