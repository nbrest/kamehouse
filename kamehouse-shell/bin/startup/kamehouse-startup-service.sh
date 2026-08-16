#!/bin/bash

# Startup script. This script is meant to be executed as a service at boot time by root.
# It can be deployed using kamehouse-startup-service-deploy.sh and then it should execute at boot.

# KAMEHOUSE_USER gets set during install kamehouse-shell
KAMEHOUSE_USER=""

source /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcessLin() {
  log.info "Starting kamehouse-startup-service.sh"
  loadKamehouseUserCfg
  checkEnv
  startTomcat
  log.info "Finished kamehouse-startup-service.sh"
}

loadKamehouseUserCfg() {
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
  log.info "KAMEHOUSE_STARTUP_USE_DEV=${KAMEHOUSE_STARTUP_USE_DEV}"
  if ! ${KAMEHOUSE_STARTUP_START_TOMCAT}; then
    return
  fi
  if ${KAMEHOUSE_STARTUP_USE_DEV}; then
    log.info "Starting tomcat dev"
    su - ${KAMEHOUSE_USER} -c /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/tomcat/tomcat-startup-dev.sh
  else
    log.info "Starting tomcat" 
    su - ${KAMEHOUSE_USER} -c /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/tomcat/tomcat-startup.sh
  fi
}

exitWithError() {
  local ERROR_MESSAGE=$1
  log.error "${ERROR_MESSAGE}"
  exit ${EXIT_ERROR}
}

main "$@"
