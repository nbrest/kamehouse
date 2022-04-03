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

KAMEHOUSE_USER=""
LOG_FILE=/home/${KAMEHOUSE_USER}/logs/rc-local.log

main() {
  log.info "Starting rc-local.sh" > ${LOG_FILE}
  
  if (( $EUID != 0 )); then
    # User not root
    exitWithError "User not root. This script can only be executed as root"
  fi

  if [ -z "${KAMEHOUSE_USER}" ]; then
    exitWithError "KAMEHOUSE_USER not set. Re run kamehouse-shell install script as non-root user"
  fi

  log.info "Setup tmpfs" >> ${LOG_FILE}
  /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/pi/startup/setup-tmpfs.sh

  log.info "Starting tomcat" >> ${LOG_FILE}
  su - ${KAMEHOUSE_USER} -c /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/tomcat-startup.sh

  log.info "Backing up server" >> ${LOG_FILE}
  su - ${KAMEHOUSE_USER} -c /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/lin/backup/backup-server.sh

  log.info "Disabling swap" >> ${LOG_FILE}
  /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/pi/startup/disable-swap.sh

  #log.info "Starting no-ip client" >> ${LOG_FILE}
  #/usr/local/bin/noip2
  # this doesn't work. so scheduled through root cron to run every 15 mins to make sure the process keeps running

  log.info "Changing permissions to log file"
  chown ${KAMEHOUSE_USER}:users ${LOG_FILE}

  log.info "Finished rc-local.sh" >> ${LOG_FILE}
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
  log.error "${ERROR_MESSAGE}" >> /root/rc-local.log
  exit 1
}

main "$@"
