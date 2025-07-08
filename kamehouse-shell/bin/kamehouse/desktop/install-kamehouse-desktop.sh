#!/bin/bash

# Execute from the root of the kamehouse git project:
# chmod a+x ./kamehouse-shell/bin/kamehouse/desktop/install-kamehouse-desktop.sh
# ./kamehouse-shell/bin/kamehouse/desktop/install-kamehouse-desktop.sh

DEFAULT_KAMEHOUSE_USERNAME=""

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
COL_YELLOW_STD="\033[0;33m"
COL_MESSAGE=${COL_GREEN}

KAMEHOUSE_DESKTOP_PATH=${HOME}/programs/kamehouse-desktop
TEMP_PATH=${HOME}/temp

KAMEHOUSE_DESKTOP_SOURCE=`pwd`
LOG_LEVEL=""

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  parseArguments "$@"
  log.info "Installing ${COL_PURPLE}kamehouse-desktop${COL_MESSAGE} to ${COL_PURPLE}${KAMEHOUSE_DESKTOP_PATH}"
  log.info "Using directory ${COL_PURPLE}${KAMEHOUSE_DESKTOP_SOURCE}${COL_MESSAGE} as the source of the scripts"
  checkSourcePath
  installKameHouseDesktop
  fixPermissions
  generateBuildInfo
  deploySourcesFromUiModule
  installKamehouseDesktopConfig
  logKameHouseDesktopStatus
  log.info "Done installing ${COL_PURPLE}kamehouse-desktop!"
}

checkSourcePath() {
  if [ ! -d "${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-shell/bin" ] || [ ! -d "${KAMEHOUSE_DESKTOP_SOURCE}/.git" ]; then
    log.error "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exit ${EXIT_ERROR}
  fi
}

installKameHouseDesktop() {
  log.info "Rebuilding desktop install directory"
  rm -r -f ${KAMEHOUSE_DESKTOP_PATH}/bin
  rm -f ${KAMEHOUSE_DESKTOP_PATH}/conf/build-info.json
  mkdir -p ${KAMEHOUSE_DESKTOP_PATH}/conf
  cp -r -f ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-desktop/bin ${KAMEHOUSE_DESKTOP_PATH}/
  cp -r -f ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-desktop/conf ${KAMEHOUSE_DESKTOP_PATH}/
  cp -r -f ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-desktop/lib ${KAMEHOUSE_DESKTOP_PATH}/
}

fixPermissions() {
  log.info "Fixing permissions"
  local KAMEHOUSE_DESKTOP_BIN_PATH=${KAMEHOUSE_DESKTOP_PATH}/bin
  chmod -R 700 ${KAMEHOUSE_DESKTOP_BIN_PATH} 
  
  local NON_SCRIPTS=`find ${KAMEHOUSE_DESKTOP_BIN_PATH} -name '.*' -prune -o -type f | grep -v -e "\.sh$\|\.py$"`;
  while read NON_SCRIPT; do
    if [ -n "${NON_SCRIPT}" ]; then
      chmod a-x ${NON_SCRIPT}
    fi
  done <<< ${NON_SCRIPTS}

  local SCRIPTS=`find ${KAMEHOUSE_DESKTOP_BIN_PATH} -name '.*' -prune -o -type f | grep -e "\.sh$\|\.py$"`;
  while read SCRIPT; do
    if [ -n "${SCRIPT}" ]; then
      chmod u+rx ${SCRIPT}
    fi
  done <<< ${SCRIPTS}

  local FUNCTIONS=`find ${KAMEHOUSE_DESKTOP_BIN_PATH} -name '.*' -prune -o -type f | grep "\-functions.py$"`
  while read FUNCTION; do
    if [ -n "${FUNCTION}" ]; then
      chmod a-x ${FUNCTION}
    fi
  done <<< ${FUNCTIONS}

  local DIRECTORIES=`find ${KAMEHOUSE_DESKTOP_BIN_PATH} -name '.*' -prune -o -type d`
  while read DIRECTORY; do
    if [ -n "${DIRECTORY}" ]; then
      chmod u+rx ${DIRECTORY}
    fi
  done <<< ${DIRECTORIES}
}

generateBuildInfo() {
  local KAMEHOUSE_DESKTOP_CONF_PATH=${KAMEHOUSE_DESKTOP_PATH}/conf
  local KAMEHOUSE_BUILD_VERSION=`getKameHouseBuildVersion`
  local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
  echo '{ "buildVersion": "'${KAMEHOUSE_BUILD_VERSION}'", "buildDate": "'${BUILD_DATE}'" }' > ${KAMEHOUSE_DESKTOP_CONF_PATH}/build-info.json
}

getKameHouseBuildVersion() {
  local KAMEHOUSE_RELEASE_VERSION=`grep -e "<version>.*1-KAMEHOUSE-SNAPSHOT</version>" pom.xml | awk '{print $1}'`
  KAMEHOUSE_RELEASE_VERSION=`echo ${KAMEHOUSE_RELEASE_VERSION:9:7}`
  local GIT_COMMIT_HASH=`git rev-parse HEAD`
  GIT_COMMIT_HASH=`echo ${GIT_COMMIT_HASH:0:9}`
  local BUILD_VERSION="${GIT_COMMIT_HASH}"
  if [ -n "${KAMEHOUSE_RELEASE_VERSION}" ]; then
    BUILD_VERSION=${KAMEHOUSE_RELEASE_VERSION}"-"${BUILD_VERSION}
  fi
  echo "${BUILD_VERSION}"
}

deploySourcesFromUiModule() {
  log.info "Deploying source files needed from ui module for desktop"
  mkdir -p "${KAMEHOUSE_DESKTOP_PATH}/lib/ui"
  cp -rf ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-ui/src/main/public/img ${KAMEHOUSE_DESKTOP_PATH}/lib/ui/img
}

installKamehouseDesktopConfig() {
  log.info "Installing kamehouse-desktop.cfg"
  if [ ! -f "${HOME}/.kamehouse/config/kamehouse-desktop.cfg" ]; then
    log.info "${COL_PURPLE}${HOME}/.kamehouse/config/kamehouse-desktop.cfg${COL_MESSAGE} not found. Creating it from template"
    mkdir -p ${HOME}/.kamehouse/config/
    cp docker/setup-kamehouse/config/kamehouse-desktop.cfg ${HOME}/.kamehouse/config/kamehouse-desktop.cfg
  else
    log.info "kamehouse-desktop.cfg file exists. skipping"
  fi
}

logKameHouseDesktopStatus() {
  log.info "Deployed kamehouse-desktop status"
  log.info "ls -lh ${COL_CYAN_STD}${KAMEHOUSE_DESKTOP_PATH}"
  ls -lh "${KAMEHOUSE_DESKTOP_PATH}"
  log.info "${COL_YELLOW_STD}kamehouse-desktop version:"
  echo -ne "${COL_YELLOW_STD}     "
  cat "${KAMEHOUSE_DESKTOP_PATH}/conf/build-info.json"
  echo -ne "${COL_NORMAL}"
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
      -p)
        KAMEHOUSE_DESKTOP_SOURCE=${HOME}/git/kamehouse
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
  echo -e "Usage: ${COL_PURPLE}install-kamehouse-desktop.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-p${COL_NORMAL} use kamehouse git prod directory instead of current dir"
}

main "$@"
