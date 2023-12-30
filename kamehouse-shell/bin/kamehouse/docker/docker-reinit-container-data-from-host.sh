#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 99
fi

LOG_PROCESS_TO_FILE=true
DATA_SOURCE="none"
REQUEST_CONFIRMATION_RX=^yes\|y$
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
    reinitMariadb
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
    exitProcess ${EXIT_PROCESS_CANCELLED}
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

  log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"chmod 755 /home/${DOCKER_USERNAME} ; chmod 700 /home/${DOCKER_USERNAME}/.ssh ; chmod 644 /home/${DOCKER_USERNAME}/.ssh/authorized_keys ; chmod 600 /home/${DOCKER_USERNAME}/.ssh/config ; chmod 600 /home/${DOCKER_USERNAME}/.ssh/id_* ; chmod 644 /home/${DOCKER_USERNAME}/.ssh/*.pub ; chmod 644 /home/${DOCKER_USERNAME}/.ssh/known_hosts\""
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "chmod 755 /home/${DOCKER_USERNAME} ; chmod 700 /home/${DOCKER_USERNAME}/.ssh ; chmod 644 /home/${DOCKER_USERNAME}/.ssh/authorized_keys ; chmod 600 /home/${DOCKER_USERNAME}/.ssh/config ; chmod 600 /home/${DOCKER_USERNAME}/.ssh/id_* ; chmod 644 /home/${DOCKER_USERNAME}/.ssh/*.pub ; chmod 644 /home/${DOCKER_USERNAME}/.ssh/known_hosts"

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

    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/keys/.*.pwd.enc /home/${DOCKER_USERNAME}/.kamehouse/keys\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/keys/.*.pwd.enc /home/${DOCKER_USERNAME}/.kamehouse/keys"
  else
    log.debug "scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/.shell/.cred ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/.shell/"
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/.shell/.cred ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/.shell/

    log.debug "scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/keys/.*.pwd.enc ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/keys"
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/keys/.*.pwd.enc ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/keys
  fi

  if [ "${DATA_SOURCE}" == "docker-init" ]; then
    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/keys/integration-test-cred.enc /home/${DOCKER_USERNAME}/.kamehouse/keys\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/docker/keys/integration-test-cred.enc /home/${DOCKER_USERNAME}/.kamehouse/keys"

    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 /home/${DOCKER_USERNAME}/.kamehouse/keys/kamehouse.pkcs12\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 /home/${DOCKER_USERNAME}/.kamehouse/keys/kamehouse.pkcs12"

    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt /home/${DOCKER_USERNAME}/.kamehouse/keys/kamehouse.crt\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt /home/${DOCKER_USERNAME}/.kamehouse/keys/kamehouse.crt"
  else
    log.debug "scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/keys/integration-test-cred.enc ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/keys"
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/keys/integration-test-cred.enc ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/keys

    log.debug "scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/keys/* ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/keys"
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/keys/* ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/keys
  fi
  
  case ${DATA_SOURCE} in
  "none")
    log.info "Skipping setup of .kamehouse/mariadb"
    ;;
  "docker-init")
    log.info "Resetting mariadb dump data from initial docker container data"
    log.debug "ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"mkdir -p /home/${DOCKER_USERNAME}/.kamehouse/mariadb/dump/old ; cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/kamehouse-shell/bin/kamehouse/sql/mariadb/dump-kamehouse.sql /home/${DOCKER_USERNAME}/.kamehouse/mariadb/dump\""
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "mkdir -p /home/${DOCKER_USERNAME}/.kamehouse/mariadb/dump/old ; cp -v -f /home/${DOCKER_USERNAME}/git/kamehouse/kamehouse-shell/bin/kamehouse/sql/mariadb/dump-kamehouse.sql /home/${DOCKER_USERNAME}/.kamehouse/mariadb/dump"
    ;;
  "docker-backup")
    log.info "Exporting mariadb data from ${HOME}/.kamehouse/docker/mariadb to the container"
    log.debug "scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/docker/mariadb ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/"
    scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/docker/mariadb ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/
    ;;
  "host-backup")
    log.info "Exporting mariadb data from ${HOME}/.kamehouse/mariadb to the container"
    log.debug "scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/mariadb ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/"
    scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/mariadb ${DOCKER_USERNAME}@localhost:/home/${DOCKER_USERNAME}/.kamehouse/
    ;;
  *) ;;
  esac
}

reinitMariadb() {
  case ${DATA_SOURCE} in
  "none")
    log.info "Skipping mariadb data reinit"
    ;;
  "docker-init"|"docker-backup"|"host-backup")
    log.info "Re-init mariadb kamehouse db from dump"
    
    log.debug "ssh -t -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C \"/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/common/mariadb/mariadb-setup-kamehouse.sh -s ; \
    /home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb-restore-kamehouse.sh\""
    ssh -t -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C "/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/common/mariadb/mariadb-setup-kamehouse.sh -s ; \
      /home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb-restore-kamehouse.sh"
    ;;
  *) ;;
  esac
}

parseArguments() {
  parseDockerProfile "$@"
  
  while getopts ":d:p:s" OPT; do
    case $OPT in
    ("d")
      DATA_SOURCE=$OPTARG
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
    exitProcess ${EXIT_INVALID_ARG}
  fi

  if [ "${DATA_SOURCE}" == "none" ]; then
    log.info "Skipping database data update"
  else 
    log.info "Database data will be updated from source: ${COL_PURPLE}${DATA_SOURCE}"
  fi

  setEnvForDockerProfile
}

printHelpOptions() {
  addHelpOption "-d (none|docker-init|docker-backup|host-backup)" "data source to reset all data. Default is none"
  printDockerProfileOption
  addHelpOption "-s" "reinit ssh keys only"
}

main "$@"
