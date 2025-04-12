#!/bin/bash

# Startup script. This script is meant to be executed as a service at boot time by root.
# It can be deployed using kamehouse-startup-service-deploy.sh and then it should execute at boot.

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

# KAMEHOUSE_USER gets set during install kamehouse-shell
KAMEHOUSE_USER=""
LOG_FILE=/home/${KAMEHOUSE_USER}/logs/kamehouse-startup-service.log

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  log.info "Starting kamehouse-startup-service.sh"
  loadKamehouseCfg
  checkEnv
  startTomcat
  setLogFilePermissions
  log.info "Finished kamehouse-startup-service.sh"
}

loadKamehouseCfg() {
  source "/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/conf/default-kamehouse.cfg"
  source /home/${KAMEHOUSE_USER}/.kamehouse/config/kamehouse.cfg
  if [ "$?" != "0" ]; then
    log.error "Error importing ~/.kamehouse/config/kamehouse.cfg" 
    exit 99
  fi
}

checkEnv() {
  log.info "Checking environment" 
  if (( $EUID != 0 )); then
    # User not root
    exitWithError "User not root. This script can only be executed as root"
  fi

  if [ -z "${KAMEHOUSE_USER}" ]; then
    exitWithError "KAMEHOUSE_USER not set. Re run kamehouse-shell install script as non-root user"
  fi

  log.info "KAMEHOUSE_USER=${KAMEHOUSE_USER}" 
}

startTomcat() {
  log.info "KAMEHOUSE_STARTUP_START_TOMCAT=${KAMEHOUSE_STARTUP_START_TOMCAT}"
  if ! ${KAMEHOUSE_STARTUP_START_TOMCAT}; then
    return
  fi  
  log.info "Starting tomcat" 
  su - ${KAMEHOUSE_USER} -c /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/tomcat-startup.sh
}

setLogFilePermissions() {
  log.info "Changing permissions to log file" 
  chown ${KAMEHOUSE_USER}:users ${LOG_FILE}
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

exitWithError() {
  local ERROR_MESSAGE=$1
  log.error "${ERROR_MESSAGE}"
  exit ${EXIT_ERROR}
}

mainWrapper() {
  main "$@" 2>&1 | tee ${LOG_FILE}
}

mainWrapper "$@"
