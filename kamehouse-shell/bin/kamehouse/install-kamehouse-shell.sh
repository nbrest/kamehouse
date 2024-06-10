#!/bin/bash

# Execute from the root of the kamehouse git project:
# chmod a+x ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
# ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh

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
COL_PURPLE_STD="\033[0;35m"
COL_MESSAGE=${COL_GREEN}

KAMEHOUSE_SHELL_PATH=${HOME}/programs/kamehouse-shell
TEMP_PATH=${HOME}/temp

KAMEHOUSE_SHELL_SOURCE=`pwd`
INSTALL_SCRIPTS_ONLY=false
LOG_LEVEL=""

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  parseCmdLineArguments "$@"
  log.info "Installing ${COL_PURPLE}kamehouse-shell${COL_MESSAGE} to ${COL_PURPLE}${KAMEHOUSE_SHELL_PATH}"
  log.info "Using directory ${COL_PURPLE}${KAMEHOUSE_SHELL_SOURCE}${COL_MESSAGE} as the source of the scripts"
  checkSourcePath
  getDefaultKameHouseUsername
  createLogsDir
  installKameHouseShell
  updateUsername
  updateLogLevel
  fixPermissions
  generateKameHouseShellPathFile
  generateBuildVersion
  if ! ${INSTALL_SCRIPTS_ONLY}; then
    installCred
    updateBashRc
  else
    log.info "Installing kamehouse-shell scripts only, so skipping the rest of the steps"
  fi
  log.info "Done installing ${COL_PURPLE}kamehouse-shell!"
}

checkSourcePath() {
  if [ ! -d "${KAMEHOUSE_SHELL_SOURCE}/kamehouse-shell/bin" ] || [ ! -d "${KAMEHOUSE_SHELL_SOURCE}/.git" ]; then
    log.error "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exit ${EXIT_ERROR}
  fi
}

getDefaultKameHouseUsername() {
  DEFAULT_KAMEHOUSE_USERNAME=`cat Dockerfile | grep "ARG KAMEHOUSE_USERNAME=" | awk -F'=' '{print $2}'`
  if [ -z "${DEFAULT_KAMEHOUSE_USERNAME}" ]; then
    log.error "Could not set default kamehouse username from Dockerfile"
    exit ${EXIT_ERROR}
  fi 
}

createLogsDir() {
  mkdir -p ${HOME}/logs
}

installKameHouseShell() {
  log.info "Rebuilding shell scripts directory"
  rm -r -f ${KAMEHOUSE_SHELL_PATH}/bin
  rm -f ${KAMEHOUSE_SHELL_PATH}/conf/path.conf
  rm -f ${KAMEHOUSE_SHELL_PATH}/conf/shell-version.txt
  mkdir -p ${KAMEHOUSE_SHELL_PATH}
  cp -r -f ${KAMEHOUSE_SHELL_SOURCE}/kamehouse-shell/bin ${KAMEHOUSE_SHELL_PATH}/
  cp -r -f ${KAMEHOUSE_SHELL_SOURCE}/kamehouse-shell/conf ${KAMEHOUSE_SHELL_PATH}/
  cp -r -f ${KAMEHOUSE_SHELL_SOURCE}/kamehouse-shell/sql ${KAMEHOUSE_SHELL_PATH}/
}

fixPermissions() {
  log.info "Fixing permissions"
  chmod -R 700 ${KAMEHOUSE_SHELL_PATH} 
  
  local NON_SCRIPTS=`find ${KAMEHOUSE_SHELL_PATH} -name '.*' -prune -o -type f | grep -v -e "\.sh$\|\.bat$\|\.awk$\|\.ps1$"`;
  while read NON_SCRIPT; do
    if [ -n "${NON_SCRIPT}" ]; then
      chmod a-x ${NON_SCRIPT}
    fi
  done <<< ${NON_SCRIPTS}

  local SCRIPTS=`find ${KAMEHOUSE_SHELL_PATH} -name '.*' -prune -o -type f | grep -e "\.sh$\|\.bat$\|\.awk$\|\.ps1$"`;
  while read SCRIPT; do
    if [ -n "${SCRIPT}" ]; then
      chmod u+rx ${SCRIPT}
    fi
  done <<< ${SCRIPTS}

  local FUNCTIONS=`find ${KAMEHOUSE_SHELL_PATH} -name '.*' -prune -o -type f | grep "\-functions.sh$"`
  while read FUNCTION; do
    if [ -n "${FUNCTION}" ]; then
      chmod a-x ${FUNCTION}
    fi
  done <<< ${FUNCTIONS}

  local DIRECTORIES=`find ${KAMEHOUSE_SHELL_PATH} -name '.*' -prune -o -type d`
  while read DIRECTORY; do
    if [ -n "${DIRECTORY}" ]; then
      chmod u+rx ${DIRECTORY}
    fi
  done <<< ${DIRECTORIES}
}

installCred() {
  log.info "Installing credentials file"
  if [ ! -f "${HOME}/.kamehouse/.shell/.cred" ]; then
    log.info "${COL_PURPLE}${HOME}/.kamehouse/.shell/.cred${COL_MESSAGE} not found. Creating it from template"
    mkdir -p ${HOME}/.kamehouse/.shell/
    cp docker/keys/.cred ${HOME}/.kamehouse/.shell/.cred
  fi
  chmod -R 700 ${HOME}/.kamehouse
}

updateUsername() {
  local USERNAME=`whoami`
  log.info "Updating username in kamehouse-shell scripts to ${COL_PURPLE}${USERNAME}"

  sed -i "s#USERNAME=\"\${DEFAULT_KAMEHOUSE_USERNAME}\"#USERNAME=\"${USERNAME}\"#g" "${KAMEHOUSE_SHELL_PATH}/bin/kamehouse/get-username.sh"
  sed -i "s#USERHOME_LIN=\"/home/\${DEFAULT_KAMEHOUSE_USERNAME}\"#USERHOME_LIN=\"/home/${USERNAME}\"#g" "${KAMEHOUSE_SHELL_PATH}/bin/kamehouse/get-userhome.sh"
  sed -i "s#USERNAME=\"\${DEFAULT_KAMEHOUSE_USERNAME}\"#USERNAME=\"${USERNAME}\"#g" "${KAMEHOUSE_SHELL_PATH}/bin/common/sudoers/www-data/su.sh"
  
  sed -i "s#DEFAULT_KAMEHOUSE_USERNAME=\"\"#DEFAULT_KAMEHOUSE_USERNAME=\"${DEFAULT_KAMEHOUSE_USERNAME}\"#g" "${KAMEHOUSE_SHELL_PATH}/bin/common/functions/kamehouse/kamehouse-functions.sh"

  sed -i "s#KAMEHOUSE_USER=\"\"#KAMEHOUSE_USER=\"${USERNAME}\"#g" "${KAMEHOUSE_SHELL_PATH}/bin/lin/startup/rc-local.sh"
  sed -i "s#KAMEHOUSE_USER#${USERNAME}#g" "${KAMEHOUSE_SHELL_PATH}/bin/lin/startup/rc-local.service"
}

updateLogLevel() {
  if [ -z "${LOG_LEVEL}" ]; then
    log.info "Using default log level ${COL_PURPLE}INFO${COL_MESSAGE} for kamehouse shell scripts"
    return
  fi
  local LEVEL=`echo "${LOG_LEVEL}" | tr '[:lower:]' '[:upper:]'`
  log.info "Updating kamehouse-shell log level to ${COL_PURPLE}${LEVEL}${COL_MESSAGE}"
  local LEVEL_NUMBER=2
  if [ "${LEVEL}" == "TRACE" ]; then
    LEVEL_NUMBER="4"
  fi
  if [ "${LEVEL}" == "DEBUG" ]; then
    LEVEL_NUMBER="3"
  fi
  if [ "${LEVEL}" == "INFO" ]; then
    LEVEL_NUMBER="2"
  fi
  if [ "${LEVEL}" == "WARN" ]; then
    LEVEL_NUMBER="1"
  fi
  if [ "${LEVEL}" == "ERROR" ]; then
    LEVEL_NUMBER="0"
  fi
  sed -i "s#LOG_LEVEL_NUMBER=2#LOG_LEVEL_NUMBER=${LEVEL_NUMBER}#g" "${KAMEHOUSE_SHELL_PATH}/bin/common/functions/log-functions.sh"
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

generateKameHouseShellPathFile() {
  log.info "Generating kamehouse-shell PATH file"
  local KAMEHOUSE_SHELL_CONF_PATH=${KAMEHOUSE_SHELL_PATH}/conf
  local KAMEHOUSE_SHELL_PATH_FILE=${KAMEHOUSE_SHELL_CONF_PATH}/path.conf

  mkdir -p ${KAMEHOUSE_SHELL_CONF_PATH}

  echo "# This file is auto generated by kamehouse-shell install script. Don't modify it" > ${KAMEHOUSE_SHELL_PATH_FILE}

  local KAMEHOUSE_SHELL_WIN_PATH=`getPathWithSubdirectories "${HOME}/programs/kamehouse-shell/bin" "/lin"`
  echo "KAMEHOUSE_SHELL_WIN_PATH=${KAMEHOUSE_SHELL_WIN_PATH}" >> ${KAMEHOUSE_SHELL_PATH_FILE}

  local KAMEHOUSE_SHELL_LIN_PATH=`getPathWithSubdirectories "${HOME}/programs/kamehouse-shell/bin" "/win"`
  echo "KAMEHOUSE_SHELL_LIN_PATH=${KAMEHOUSE_SHELL_LIN_PATH}" >> ${KAMEHOUSE_SHELL_PATH_FILE}
}

###########################################################################
# IMPORTANT: If I block a path here, also block it to csv-kamehouse-shell.sh
###########################################################################
getPathWithSubdirectories() {
  local BASE_PATH=$1
  local PATHS_TO_SKIP_REGEX=$2
  if [ ! -d "${BASE_PATH}" ]; then
    return
  fi
  # List all directories
  local PATH_WITH_SUBDIRS=$(find ${BASE_PATH} -name '.*' -prune -o -type d)
  # Filter bashrc
  PATH_WITH_SUBDIRS=$(echo "$PATH_WITH_SUBDIRS" | grep -v /common/bashrc)
  # Filter docker container scripts
  PATH_WITH_SUBDIRS=$(echo "$PATH_WITH_SUBDIRS" | grep -v /kamehouse/docker/docker-container)
  PATH_WITH_SUBDIRS=$(echo "$PATH_WITH_SUBDIRS" | grep -v /kamehouse/docker/release/java8-release/bin)
  PATH_WITH_SUBDIRS=$(echo "$PATH_WITH_SUBDIRS" | grep -v /kamehouse/docker/release/java8-release/docker)
  PATH_WITH_SUBDIRS=$(echo "$PATH_WITH_SUBDIRS" | grep -v /kamehouse/docker/release/java11-release/bin)
  PATH_WITH_SUBDIRS=$(echo "$PATH_WITH_SUBDIRS" | grep -v /kamehouse/docker/release/java11-release/docker)  
  # Filter path to skip parameter
  PATH_WITH_SUBDIRS=$(echo "$PATH_WITH_SUBDIRS" | grep -v -e "${PATHS_TO_SKIP_REGEX}") 
  
  # Filter .. directory
  PATH_WITH_SUBDIRS=$(echo "$PATH_WITH_SUBDIRS" | grep -v '/\..*')
  
  # Replace \n with :  
  PATH_WITH_SUBDIRS=$(echo "$PATH_WITH_SUBDIRS" | tr '\n' ':')

  # Remove last :
  PATH_WITH_SUBDIRS=$(echo "$PATH_WITH_SUBDIRS" | sed '$s/.$//')

  echo "${PATH_WITH_SUBDIRS}"
} 

generateBuildVersion() {
  local KAMEHOUSE_SHELL_CONF_PATH=${KAMEHOUSE_SHELL_PATH}/conf
  local SHELL_VERSION_FILE="${KAMEHOUSE_SHELL_CONF_PATH}/shell-version.txt"
  local KAMEHOUSE_BUILD_VERSION=`getKameHouseBuildVersion`
  echo "buildVersion=${KAMEHOUSE_BUILD_VERSION}" > ${SHELL_VERSION_FILE}
  local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
  echo "buildDate=${BUILD_DATE}" >> ${SHELL_VERSION_FILE}
}

getKameHouseBuildVersion() {
  local KAMEHOUSE_RELEASE_VERSION=`grep -e "<version>.*1-KAMEHOUSE-SNAPSHOT</version>" pom.xml | awk '{print $1}'`
  KAMEHOUSE_RELEASE_VERSION=`echo ${KAMEHOUSE_RELEASE_VERSION:9:6}`
  local GIT_COMMIT_HASH=`git rev-parse --short HEAD`
  local BUILD_VERSION="${GIT_COMMIT_HASH}"
  if [ -n "${KAMEHOUSE_RELEASE_VERSION}" ]; then
    BUILD_VERSION=${KAMEHOUSE_RELEASE_VERSION}"-"${BUILD_VERSION}
  fi
  echo "${BUILD_VERSION}"
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

parseCmdLineArguments() {
  while getopts ":hol:p" OPT; do
    case $OPT in
    ("h")
      printHelpMenu
      exit ${EXIT_SUCCESS}
      ;;
    ("o")
      INSTALL_SCRIPTS_ONLY=true
      ;;
    ("l")
      LOG_LEVEL=$OPTARG
      ;;      
    ("p")
      KAMEHOUSE_SHELL_SOURCE=${HOME}/git/kamehouse
      ;;
    (\?)
      log.error "Invalid argument $OPTARG"
      exit ${EXIT_INVALID_ARG}
      ;;
    esac
  done
}

printHelpMenu() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}install-kamehouse-shell.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-o${COL_NORMAL} only install kamehouse shell scripts. Don't modify the shell"
  echo -e "     ${COL_BLUE}-l [ERROR|WARN|INFO|DEBUG|TRACE]${COL_NORMAL} set log level for scripts. Default is INFO"
  echo -e "     ${COL_BLUE}-p${COL_NORMAL} use kamehouse git prod directory instead of current dir"
}

main "$@"
