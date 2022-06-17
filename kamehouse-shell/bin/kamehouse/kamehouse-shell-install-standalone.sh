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

KAMEHOUSE_SHELL_PATH=${HOME}/programs/kamehouse-shell
KAMEHOUSE_SHELL_SOURCE="."

main() {
  log.info "Installing kamehouse-shell scripts to ${HOME}/programs/kamehouse-shell"

  cd ${HOME}/git/kamehouse
  
  pullLatestVersion
  checkSourcePath
  installKameHouseShell
  fixPermissions
  #setLogLevelToTrace

  log.info "Finished installing kamehouse-shell scripts"
}

checkSourcePath() {
  if [ ! -d "${KAMEHOUSE_SHELL_SOURCE}/kamehouse-shell/bin" ] || [ ! -d "${KAMEHOUSE_SHELL_SOURCE}/.git" ]; then
    log.error "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exit 1
  fi
}

pullLatestVersion() {
  log.info "Pulling latest kamehouse version from git"
  git pull origin dev
}

installKameHouseShell() {
  log.info "Rebuilding shell scripts directory"
  rm -r -f ${KAMEHOUSE_SHELL_PATH}
  mkdir -p ${KAMEHOUSE_SHELL_PATH}
  cp -r -f ${KAMEHOUSE_SHELL_SOURCE}/kamehouse-shell/bin ${KAMEHOUSE_SHELL_PATH}/
}

fixPermissions() {
  log.info "Fixing permissions on ${KAMEHOUSE_SHELL_PATH}"
  chmod -R a+x ${KAMEHOUSE_SHELL_PATH}
}

setLogLevelToTrace() {
  log.info "Updating scripts log level to trace"
  sed -i '' "s~LOG_LEVEL_NUMBER=2~LOG_LEVEL_NUMBER=4~" ${KAMEHOUSE_SHELL_PATH}/bin/common/log-functions.sh
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

main "$@"