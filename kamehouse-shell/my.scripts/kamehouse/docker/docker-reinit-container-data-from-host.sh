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

USE_DOCKER_MYSQL_DUMP=false
REQUEST_CONFIRMATION_RX=^yes\|y$

mainProcess() {
  log.info "Re-init data in the container from the host file system/db"
  log.info "This script should be executed from the host's command line, not inside the docker container"

  requestConfirmation
  reinitSsh
  reinitMyScripts
  reinitKameHouseFolder
  reinitHomeSynced
  reinitHttpd
  reinitMysql
}

requestConfirmation() {
  log.warn "${COL_YELLOW}This process will reset the data in the container including the kamehouse database"
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
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/.ssh/* localhost:/home/nbrest/.ssh
  ssh -p ${DOCKER_PORT_SSH} nbrest@localhost -C 'chmod 0600 /home/nbrest/.ssh/id_rsa'
  log.info "Connect through ssh from container to host to add host key to known hosts for automated ssh commands from the container"
  ssh -p ${DOCKER_PORT_SSH} nbrest@localhost -C 'source .kamehouse/.kamehouse-docker-container-env ; ssh-keyscan $DOCKER_HOST_IP >> ~/.ssh/known_hosts ; ssh $DOCKER_HOST_IP -C echo ssh keys configured successfully'
  log.warn "If the last command didn't display 'ssh keys configured successfully' then login to the container and ssh from the container to the host to add the host key to known hosts file"
}

reinitMyScripts() {
  log.info "Setup my.scripts folder"
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/my.scripts/.cred/.cred localhost:/home/nbrest/my.scripts/.cred/
}

reinitKameHouseFolder() {
  log.info "Setup .kamehouse folder"
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/.unlock.screen.pwd.enc localhost:/home/nbrest/.kamehouse
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/.vnc.server.pwd.enc localhost:/home/nbrest/.kamehouse
}

reinitHomeSynced() {
  log.info "Setup home-synced folder"
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/integration-test-cred.enc localhost:/home/nbrest/home-synced/.kamehouse
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/.kamehouse/keys/* localhost:/home/nbrest/home-synced/.kamehouse/keys
  if ${USE_DOCKER_MYSQL_DUMP}; then
    log.info "Exporting mysql data from ${HOME}/home-synced/docker/mysql to the container"
    scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/docker/mysql localhost:/home/nbrest/home-synced/
  else
    log.info "Exporting mysql data from ${HOME}/home-synced/mysql to the container"
    scp -C -r -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/mysql localhost:/home/nbrest/home-synced/
  fi
}

reinitHttpd() {
  log.info "Setup httpd"
  scp -C -P ${DOCKER_PORT_SSH} ${HOME}/home-synced/httpd/.htpasswd localhost:/home/nbrest/home-synced/httpd
}

reinitMysql() {
  log.info "Re-init mysql kamehouse db from dump"
  ssh -p ${DOCKER_PORT_SSH} nbrest@localhost -C 'sudo /home/nbrest/my.scripts/common/mysql/add-mysql-user-nikolqs.sh'
  ssh -p ${DOCKER_PORT_SSH} nbrest@localhost -C 'sudo mysql -v < /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/setup-kamehouse.sql'
  ssh -p ${DOCKER_PORT_SSH} nbrest@localhost -C 'sudo mysql kameHouse < /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/spring-session.sql'
  ssh -p ${DOCKER_PORT_SSH} nbrest@localhost -C '/home/nbrest/my.scripts/kamehouse/mysql-restore-kamehouse.sh'
}

parseArguments() {
  while getopts ":dh" OPT; do
    case $OPT in
    ("d")
      USE_DOCKER_MYSQL_DUMP=true
      ;;
    ("h")
      parseHelp
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"
  echo -e "     ${COL_BLUE}-d${COL_NORMAL} use docker mysql dump backed up in the host, instead of the host's native mysql dump"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
}

main "$@"
