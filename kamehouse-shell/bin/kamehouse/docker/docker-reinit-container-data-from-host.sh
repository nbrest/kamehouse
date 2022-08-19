#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

DATA_SOURCE="none"
REQUEST_CONFIRMATION_RX=^yes\|y$
DOCKER_PROFILE="${DEFAULT_DOCKER_PROFILE}"
REINIT_SSH_KEYS_ONLY=false

mainProcess() {
  log.info "Re-init data in the container from the host file system/db"
  log.info "This script should be executed from the host's command line, not inside the docker container"
  log.info "When run with '-d docker-init' it resets all the data to the initial container state"
  
  requestConfirmation
  if ${REINIT_SSH_KEYS_ONLY}; then
    reinitSsh
  else
    reinitSsh
    reinitKameHouseFolder
    reinitHomeSynced
    reinitHttpd
    reinitMysql
  fi
}

requestConfirmation() {
  log.warn "${COL_YELLOW}This process will reset the data in the container including the kamehouse database if -d option was set"
  log.info "Do you want to proceed? (${COL_BLUE}Yes${COL_DEFAULT_LOG}/${COL_RED}No${COL_DEFAULT_LOG}): "
  read SHOULD_PROCEED
  SHOULD_PROCEED=`echo "${SHOULD_PROCEED}" | tr '[:upper:]' '[:lower:]'`
  if [[ "${SHOULD_PROCEED}" =~ ${REQUEST_CONFIRMATION_RX} ]]; then
    log.info "Proceeding"
  else
    log.warn "${COL_PURPLE}${SCRIPT_NAME}${COL_DEFAULT_LOG} cancelled by the user"
    exitProcess 2
  fi
}

reinitSsh() {
  log.info "Setup .ssh folder"
  ssh-keygen -f "${HOME}/.ssh/known_hosts" -R "[localhost]:${DOCKER_PORT_SSH}"
  if [ ! -f "${HOME}/.ssh/id_rsa.pkcs8" ] || [ ! -f "${HOME}/.ssh/id_rsa.pub.pkcs8" ]; then
    log.info "Couldn't find rsa keys pkcs8 format. Attempting to generate them"
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/convert-rsa-keys-to-pkcs8.sh
  fi

  log.debug "scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.ssh/* ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.ssh"
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.ssh/* ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.ssh

  log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"chmod 0600 /home/${DOCKER_USERNAME}/.ssh/id_rsa\""
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "chmod 0600 /home/${DOCKER_USERNAME}/.ssh/id_rsa"

  log.info "Connect through ssh from container to host to add host key to known hosts for automated ssh commands from the container"
  
  log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'source .kamehouse/.kamehouse-docker-container-env ; ssh-keyscan $DOCKER_HOST_IP >> ~/.ssh/known_hosts ; ssh $DOCKER_HOST_USERNAME@$DOCKER_HOST_IP -C echo ssh keys configured successfully'"
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'source .kamehouse/.kamehouse-docker-container-env ; ssh-keyscan $DOCKER_HOST_IP >> ~/.ssh/known_hosts ; ssh $DOCKER_HOST_USERNAME@$DOCKER_HOST_IP -C echo ssh keys configured successfully'

  log.warn "If the last command didn't display 'ssh keys configured successfully' then login to the container and ssh from the container to the host using DOCKER_HOST_IP to add the host key to known hosts file"
}

reinitKameHouseFolder() {
  log.info "Setup .kamehouse folder"
  if [ "${DATA_SOURCE}" == "docker-init" ]; then
    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/keys/.cred /home/${DOCKER_USERNAME}/.kamehouse/.shell/\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/keys/.cred /home/${DOCKER_USERNAME}/.kamehouse/.shell/"

    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/keys/.*.pwd.enc /home/${DOCKER_USERNAME}/.kamehouse\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/keys/.*.pwd.enc /home/${DOCKER_USERNAME}/.kamehouse"
  else
    log.debug "scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/.shell/.cred ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/.shell/"
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/.shell/.cred ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/.shell/

    log.debug "scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/.*.pwd.enc ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse"
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/.*.pwd.enc ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse
  fi
}

reinitHomeSynced() {
  log.info "Setup home-synced folder"
  if [ "${DATA_SOURCE}" == "docker-init" ]; then
    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/keys/integration-test-cred.enc /home/${DOCKER_USERNAME}/home-synced/.kamehouse\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/keys/integration-test-cred.enc /home/${DOCKER_USERNAME}/home-synced/.kamehouse"

    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 /home/${DOCKER_USERNAME}/home-synced/.kamehouse/keys/kamehouse.pkcs12\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 /home/${DOCKER_USERNAME}/home-synced/.kamehouse/keys/kamehouse.pkcs12"

    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt /home/${DOCKER_USERNAME}/home-synced/.kamehouse/keys/kamehouse.crt\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt /home/${DOCKER_USERNAME}/home-synced/.kamehouse/keys/kamehouse.crt"
  else
    log.debug "scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/integration-test-cred.enc ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/.kamehouse"
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/integration-test-cred.enc ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/.kamehouse

    log.debug "scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/keys/* ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/.kamehouse/keys"
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/keys/* ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/.kamehouse/keys
  fi
  
  case ${DATA_SOURCE} in
  "none")
    log.info "Skipping setup of home-synced/mysql"
    ;;
  "docker-init")
    log.info "Resetting mysql dump data from initial docker container data"
    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"mkdir -p /home/${DOCKER_USERNAME}/home-synced/mysql/dump/old ; cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/mysql/dump-kamehouse.sql /home/${DOCKER_USERNAME}/home-synced/mysql/dump\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "mkdir -p /home/${DOCKER_USERNAME}/home-synced/mysql/dump/old ; cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/mysql/dump-kamehouse.sql /home/${DOCKER_USERNAME}/home-synced/mysql/dump"
    ;;
  "docker-backup")
    log.info "Exporting mysql data from ${HOME}/home-synced/docker/mysql to the container"
    log.debug "scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/docker/mysql ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/"
    scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/docker/mysql ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/
    ;;
  "host-backup")
    log.info "Exporting mysql data from ${HOME}/home-synced/mysql to the container"
    log.debug "scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/mysql ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/"
    scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/mysql ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/
    ;;
  *) ;;
  esac
}

reinitHttpd() {
  log.info "Setup httpd"
  if [ "${DATA_SOURCE}" == "docker-init" ]; then
    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"cp -v -f /var/www/html/.htpasswd /home/${DOCKER_USERNAME}/home-synced/httpd/\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "cp -v -f /var/www/html/.htpasswd /home/${DOCKER_USERNAME}/home-synced/httpd/"
  else
    log.debug "scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/httpd/.htpasswd ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/httpd"
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/httpd/.htpasswd ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/home-synced/httpd
  fi
}

reinitMysql() {
  case ${DATA_SOURCE} in
  "none")
    log.info "Skipping mysql data reinit"
    ;;
  "docker-init"|"docker-backup"|"host-backup")
    log.info "Re-init mysql kamehouse db from dump"
    
    log.debug "ssh -t -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/common/mysql/add-mysql-user-nikolqs.sh ; \
    /home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mysql-setup-kamehouse.sh ; \
    /home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mysql-restore-kamehouse.sh\""
    ssh -t -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/common/mysql/add-mysql-user-nikolqs.sh ; \
      /home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mysql-setup-kamehouse.sh ; \
      /home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mysql-restore-kamehouse.sh"
    ;;
  *) ;;
  esac
}

parseArguments() {
  while getopts ":d:p:s" OPT; do
    case $OPT in
    ("d")
      DATA_SOURCE=$OPTARG
      ;;
    ("p")
      DOCKER_PROFILE=$OPTARG
      ;;
    ("s")
      log.info "${COL_RED}Only ssh keys will be reinited"
      REINIT_SSH_KEYS_ONLY=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  if [ "${DATA_SOURCE}" != "none" ] &&
    [ "${DATA_SOURCE}" != "docker-init" ] &&
    [ "${DATA_SOURCE}" != "docker-backup" ] &&
    [ "${DATA_SOURCE}" != "host-backup" ]; then
    log.error "Option -d [data source] has an invalid value of ${DATA_SOURCE}"
    printHelp
    exitProcess 1
  fi

  if [ "${DATA_SOURCE}" == "none" ]; then
    log.info "Skipping database data update"
  else 
    log.info "Database data will be updated from source: ${COL_PURPLE}${DATA_SOURCE}"
  fi

  if [ "${DOCKER_PROFILE}" != "ci" ] &&
    [ "${DOCKER_PROFILE}" != "dev" ] &&
    [ "${DOCKER_PROFILE}" != "demo" ] &&
    [ "${DOCKER_PROFILE}" != "prod" ] &&
    [ "${DOCKER_PROFILE}" != "prod-ext" ]; then
    log.error "Option -p [profile] has an invalid value of ${DOCKER_PROFILE}"
    printHelp
    exitProcess 1
  fi

  if [ "${DOCKER_PROFILE}" == "ci" ]; then
    DOCKER_PORT_SSH=15022
  fi

  if [ "${DOCKER_PROFILE}" == "demo" ]; then
    DOCKER_PORT_SSH=12022
  fi

  if [ "${DOCKER_PROFILE}" == "prod" ]; then
    DOCKER_PORT_SSH=7022
  fi

  if [ "${DOCKER_PROFILE}" == "prod-ext" ]; then
    DOCKER_PORT_SSH=7022
  fi  
}

printHelpOptions() {
  addHelpOption "-d (none|docker-init|docker-backup|host-backup)" "data source to reset all data. Default is none"
  addHelpOption "-p ${DOCKER_PROFILES_LIST}" "default profile is ${DEFAULT_DOCKER_PROFILE}"
  addHelpOption "-s" "reinit ssh keys only"
}

main "$@"
