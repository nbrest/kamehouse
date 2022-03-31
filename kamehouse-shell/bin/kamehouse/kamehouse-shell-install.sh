#!/bin/bash

# Execute from the root of the kamehouse git project:
# chmod a+x ./kamehouse-shell/bin/kamehouse/kamehouse-shell-install.sh
# ./kamehouse-shell/bin/kamehouse/kamehouse-shell-install.sh

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
# CREATE dummy ./kamehouse/.shell/.cred if it doesn't exist from a template

main() {
  parseArguments "$@"
  logStep "Installing ${COL_PURPLE}kamehouse-shell${COL_MESSAGE} to ${COL_PURPLE}${KAMEHOUSE_SHELL_PATH}"
  logScriptParameters
  checkSourcePath
  installKameHouseShell
  fixPermissions
  installCred
  updateBashRc
  logStep "Done!"
}

logScriptParameters() {
  logStep "KAMEHOUSE_SHELL_SOURCE: ${KAMEHOUSE_SHELL_SOURCE}"
}

checkSourcePath() {
  if [ ! -d "${KAMEHOUSE_SHELL_SOURCE}/kamehouse-shell/bin" ] || [ ! -d "${KAMEHOUSE_SHELL_SOURCE}/.git" ]; then
    logError "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exit 1
  fi
}

installKameHouseShell() {
  logStep "Rebuilding shell scripts directory"
  rm -r -f ${KAMEHOUSE_SHELL_PATH}
  mkdir -p ${KAMEHOUSE_SHELL_PATH}
  cp -r -f ${KAMEHOUSE_SHELL_SOURCE}/kamehouse-shell/bin ${KAMEHOUSE_SHELL_PATH}/
}

fixPermissions() {
  logStep "Fixing permissions"
  chmod a+x -R ${KAMEHOUSE_SHELL_PATH}
}

installCred() {
  logStep "Installing credentials file"
  if [ ! -f "${HOME}/.kamehouse/.shell/.cred" ]; then
    logStep "${COL_PURPLE}${HOME}/.kamehouse/.shell/.cred${COL_MESSAGE} not found. Creating it from template"
    mkdir -p ${HOME}/.kamehouse/.shell/
    cp docker/keys/.cred ${HOME}/.kamehouse/.shell/.cred
  fi
}

updateBashRc() {
  logStep "Updating ${COL_PURPLE}${HOME}/.bashrc"
  if [ ! -f "${HOME}/.bashrc" ]; then
    logStep "${COL_PURPLE}${HOME}/.bashrc${COL_MESSAGE} not found. Creating one"
    echo "" > ${HOME}/.bashrc
    echo "source \${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh" >> ${HOME}/.bashrc
  else 
    cat ${HOME}/.bashrc | grep "/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh" > /dev/null
    if [ "$?" != "0" ]; then
      logStep "Adding bashrc/bashrc.sh to ${COL_PURPLE}${HOME}/.bashrc"
      echo "" >> ${HOME}/.bashrc
      echo "source \${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh" >> ${HOME}/.bashrc
    else 
      logStep "${COL_PURPLE}${HOME}/.bashrc${COL_MESSAGE} already sources ${COL_PURPLE}${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh${COL_MESSAGE}. No need to update"
    fi
  fi
}

logStep() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

logError() {
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
      logError "Invalid argument $OPTARG"
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