#!/bin/bash

# Startup script. This script is meant to be executed as a service at boot time by root.
# It can be deployed using rc-local-deploy.sh and then it should execute at boot.

COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_MESSAGE=${COL_GREEN}

# KAMEHOUSE_USER gets set during install kamehouse-shell
KAMEHOUSE_USER=""
LOG_FILE=/root/rc-local.log
LOGS_DIR=/home/${KAMEHOUSE_USER}/logs
FINAL_LOG_FILE=${LOGS_DIR}/rc-local.log

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  log.info "Starting rc-local.sh" > ${LOG_FILE}
  checkEnv
  setupTmpfs
  disableSwap
  startTomcat
  #backupServer
  moveLogFile
}

checkEnv() {
  log.info "Checking environment" >> ${LOG_FILE}
  if (( $EUID != 0 )); then
    # User not root
    exitWithError "User not root. This script can only be executed as root"
  fi

  if [ -z "${KAMEHOUSE_USER}" ]; then
    exitWithError "KAMEHOUSE_USER not set. Re run kamehouse-shell install script as non-root user"
  fi

  log.info "KAMEHOUSE_USER=${KAMEHOUSE_USER}" >> ${LOG_FILE}
}

setupTmpfs() {
  log.info "Setup tmpfs" >> ${LOG_FILE}
  /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/lin/pi/startup/pi-setup-tmpfs.sh
}

startTomcat() {
  log.info "Starting tomcat" >> ${LOG_FILE}
  su - ${KAMEHOUSE_USER} -c /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/tomcat-startup.sh
}

backupServer() {
  log.info "Backing up server" >> ${LOG_FILE}
  su - ${KAMEHOUSE_USER} -c /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/lin/backup/backup-server.sh
}

disableSwap() {
  log.info "Disabling swap" >> ${LOG_FILE}
  /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/lin/pi/startup/pi-disable-swap.sh
}

moveLogFile() {
  log.info "Moving ${LOG_FILE} file to ${FINAL_LOG_FILE}" >> ${LOG_FILE}
  if [ -d "${LOGS_DIR}" ]; then
    log.info "Finished rc-local.sh" >> ${LOG_FILE}
    mv ${LOG_FILE} ${FINAL_LOG_FILE}
    chown ${KAMEHOUSE_USER}:users ${FINAL_LOG_FILE}
  else
    log.info "ERROR: ${LOGS_DIR} doesn't exist" >> ${LOG_FILE}
    log.info "Finished rc-local.sh" >> ${LOG_FILE}
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

exitWithError() {
  local ERROR_MESSAGE=$1
  log.error "${ERROR_MESSAGE}"
  log.error "${ERROR_MESSAGE}" >> ${LOG_FILE}
  exit ${EXIT_ERROR}
}

main "$@"
