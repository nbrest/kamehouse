#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/my.scripts/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

DATA_SOURCE="none"
REQUEST_CONFIRMATION_RX=^yes\|y$

mainProcess() {
  log.info "Re-init data in the container from the host file system/db"
  log.info "This script should be executed from the host's command line, not inside the docker container"
  log.info "When run with '-d docker-init' it resets all the data to the initial container state"
  
  requestConfirmation
  reinitSsh
  reinitMyScripts
  reinitKameHouseFolder
  reinitHomeSynced
  reinitHttpd
  reinitMysql
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
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.ssh/* ${DOCKER_USERNAME}@localhost:/home/nbrest/.ssh
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'chmod 0600 /home/nbrest/.ssh/id_rsa'
  log.info "Connect through ssh from container to host to add host key to known hosts for automated ssh commands from the container"
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'source .kamehouse/.kamehouse-docker-container-env ; ssh-keyscan $DOCKER_HOST_IP >> ~/.ssh/known_hosts ; ssh $DOCKER_HOST_IP -C echo ssh keys configured successfully'
  log.warn "If the last command didn't display 'ssh keys configured successfully' then login to the container and ssh from the container to the host using DOCKER_HOST_IP to add the host key to known hosts file"
}

reinitMyScripts() {
  log.info "Setup my.scripts folder"
  if [ "${DATA_SOURCE}" == "docker-init" ]; then
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'cp -v -f /home/nbrest/git/java.web.kamehouse/docker/keys/.cred /home/nbrest/my.scripts/.cred/'
  else
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/my.scripts/.cred/.cred ${DOCKER_USERNAME}@localhost:/home/nbrest/my.scripts/.cred/
  fi
}

reinitKameHouseFolder() {
  log.info "Setup .kamehouse folder"
  if [ "${DATA_SOURCE}" == "docker-init" ]; then
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'cp -v -f /home/nbrest/git/java.web.kamehouse/docker/keys/.*.pwd.enc /home/nbrest/.kamehouse'
  else
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.kamehouse/.*.pwd.enc ${DOCKER_USERNAME}@localhost:/home/nbrest/.kamehouse
  fi
}

reinitHomeSynced() {
  log.info "Setup home-synced folder"
  if [ "${DATA_SOURCE}" == "docker-init" ]; then
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'cp -v -f /home/nbrest/git/java.web.kamehouse/docker/keys/integration-test-cred.enc /home/nbrest/home-synced/.kamehouse'
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'cp -v -f /home/nbrest/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 /home/nbrest/home-synced/.kamehouse/keys/kamehouse.pkcs12'
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'cp -v -f /home/nbrest/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt /home/nbrest/home-synced/.kamehouse/keys/kamehouse.crt'
  else
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/integration-test-cred.enc ${DOCKER_USERNAME}@localhost:/home/nbrest/home-synced/.kamehouse
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/keys/* ${DOCKER_USERNAME}@localhost:/home/nbrest/home-synced/.kamehouse/keys
  fi
  
  case ${DATA_SOURCE} in
  "none")
    log.info "Skipping setup of home-synced/mysql"
    ;;
  "docker-init")
    log.info "Resetting mysql dump data from initial docker container data"
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'mkdir -p /home/nbrest/home-synced/mysql/dump/old ; cp -v -f /home/nbrest/git/java.web.kamehouse/docker/mysql/dump-kamehouse.sql /home/nbrest/home-synced/mysql/dump'
    ;;
  "docker-backup")
    log.info "Exporting mysql data from ${HOME}/home-synced/docker/mysql to the container"
    scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/docker/mysql ${DOCKER_USERNAME}@localhost:/home/nbrest/home-synced/
    ;;
  "host-backup")
    log.info "Exporting mysql data from ${HOME}/home-synced/mysql to the container"
    scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/mysql ${DOCKER_USERNAME}@localhost:/home/nbrest/home-synced/
    ;;
  *) ;;
  esac
}

reinitHttpd() {
  log.info "Setup httpd"
  if [ "${DATA_SOURCE}" == "docker-init" ]; then
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'cp -v -f /var/www/html/.htpasswd /home/nbrest/home-synced/httpd/'
  else
    scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/httpd/.htpasswd ${DOCKER_USERNAME}@localhost:/home/nbrest/home-synced/httpd
  fi
}

reinitMysql() {
  case ${DATA_SOURCE} in
  "none")
    log.info "Skipping mysql data reinit"
    ;;
  "docker-init"|"docker-backup"|"host-backup")
    log.info "Re-init mysql kamehouse db from dump"
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'sudo /home/nbrest/my.scripts/common/mysql/add-mysql-user-nikolqs.sh'
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'sudo mysql -v < /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/setup-kamehouse.sql'
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C 'sudo mysql kameHouse < /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/spring-session.sql'
    ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost -C '/home/nbrest/my.scripts/kamehouse/mysql-restore-kamehouse.sh' 
    ;;
  *) ;;
  esac
}

parseArguments() {
  while getopts ":d:mh" OPT; do
    case $OPT in
    ("d")
      DATA_SOURCE=$OPTARG
      ;;
    ("m")
      MYSQL_REINIT_SKIP=true
      ;;
    ("h")
      parseHelp
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done

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
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"
  echo -e "     ${COL_BLUE}-d (none|docker-init|docker-backup|host-backup)${COL_NORMAL} data source to reset all data"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
}

main "$@"
