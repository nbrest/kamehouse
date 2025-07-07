#!/bin/bash
# This script runs while building the dockerfile

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

KAMEHOUSE_USER=""
DOCKER_IMAGE_TAG="latest"

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  parseArguments "$@"
  setEnvironment
  setupKameHouseUserHome
  setupGitRepo
  installKameHouseShell
  setContainerDefaults
  deployKameHouse
  clearTempFiles
  createSamplePlaylists
  configureSudoers
  installGroot
  configureMariadb
  startMariadb
  setupKameHouseMariadb
}

setEnvironment() {
  log.info "Setting up environment"
  suCmd "echo DOCKER_IMAGE_TAG=${DOCKER_IMAGE_TAG} >> /home/${KAMEHOUSE_USER}/.env"
}

setupKameHouseUserHome() {
  log.info "Setting up kamehouse user home"
  mkdir -p /home/${KAMEHOUSE_USER}/git 
  chmod a+xwr /home/${KAMEHOUSE_USER}/git 
  chmod a+xr /home/${KAMEHOUSE_USER} 
  mkdir -p /home/${KAMEHOUSE_USER}/logs 
  chmod a+xr /home/${KAMEHOUSE_USER}/logs 
  rm -rf /home/${KAMEHOUSE_USER}/git/kamehouse
  fixPermissions
}

setupGitRepo() {
  log.info "Setting up kamehouse git repo"
  cd /home/${KAMEHOUSE_USER}/git 
  git clone https://github.com/nbrest/kamehouse.git 
  cd /home/${KAMEHOUSE_USER}/git/kamehouse 
  log.info "Checking out git branch for tag ${DOCKER_IMAGE_TAG}"
  if [ "${DOCKER_IMAGE_TAG}" == "latest" ]; then
    git checkout dev
  else
    git checkout tags/${DOCKER_IMAGE_TAG} -b ${DOCKER_IMAGE_TAG}
  fi
  git branch -D master
  fixPermissions
}

installKameHouseShell() {
  log.info "Installing kamehouse shell"
  suCmd "chmod a+x /home/${KAMEHOUSE_USER}/git/kamehouse/kamehouse-shell/bin/kamehouse/shell/install-kamehouse-shell.sh"
  suCmd "cd /home/${KAMEHOUSE_USER}/git/kamehouse ; ./kamehouse-shell/bin/kamehouse/shell/install-kamehouse-shell.sh"
}

setContainerDefaults() {
  log.info "Setting up container defaults"
  suCmd "/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/docker/docker-container/docker-init-kamehouse-folder-to-defaults.sh"
}

deployKameHouse() {
  log.info "Deploying kamehouse"
  suCmd "/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/deploy/deploy-kamehouse.sh -c -p docker -m shell" 
  suCmd "/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/deploy/deploy-kamehouse.sh -c -p docker"
}

clearTempFiles() {
  log.info "Clearing up temp files"
  suCmd "cd /home/${KAMEHOUSE_USER}/git/kamehouse ; /home/${KAMEHOUSE_USER}/programs/apache-maven/bin/mvn clean"
  rm -rf /home/${KAMEHOUSE_USER}/.m2/repository/com/nicobrest 
}

createSamplePlaylists() {
  log.info "Setting up sample playlists"
  suCmd "/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/media/create-sample-video-playlists.sh"
}

configureSudoers() {
  log.info "Setting up sudoers"
  /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/deploy/set-kamehouse-sudoers-permissions.sh -u ${KAMEHOUSE_USER}
}

installGroot() {
  log.info "Installing groot"
  /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/groot/install-kamehouse-groot.sh -u ${KAMEHOUSE_USER} 
}

configureMariadb() {
  log.info "Setting up mariadb"
  # Open mariadb to external connections
  sed -i "s#bind-address            = 127.0.0.1#bind-address            = 0.0.0.0#g" /etc/mysql/mariadb.conf.d/50-server.cnf 
}

startMariadb() {
  log.info "Starting mariadb"
  service mariadb start 
  sleep 5 
  service mariadb start
  sleep 5 
  service mariadb start 
  sleep 5 
  service mariadb start 
  sleep 5 
  service mariadb start 
}

setupKameHouseMariadb() {
  log.info "Setting up kamehouse mariadb initial dump"
  mariadb -e"set @kameHousePass = '`cat /home/${KAMEHOUSE_USER}/docker/keys/.kamehouse-secrets.cfg | grep MARIADB_PASS_KAMEHOUSE | cut -d '=' -f 2`'; `cat /home/${KAMEHOUSE_USER}/git/kamehouse/kamehouse-shell/sql/mariadb/add-kamehouse-user.sql`" 
  mariadb < /home/${KAMEHOUSE_USER}/git/kamehouse/kamehouse-shell/sql/mariadb/create-kamehouse-schema.sql 
  mariadb kamehouse < /home/${KAMEHOUSE_USER}/git/kamehouse/kamehouse-shell/sql/mariadb/spring-session.sql
  mariadb kamehouse < /home/${KAMEHOUSE_USER}/git/kamehouse/kamehouse-shell/sql/mariadb/dump-kamehouse.sql
}

fixPermissions() {
  log.info "Fixing permissions"
  chown ${KAMEHOUSE_USER}:users -R /home/${KAMEHOUSE_USER}/  
}

suCmd() {
  local COMMAND=$1
  sudo su - ${KAMEHOUSE_USER} -c "${COMMAND}"
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
      -u)
        KAMEHOUSE_USER="${CURRENT_OPTION_ARG}"
        ;;
      -t)
        DOCKER_IMAGE_TAG="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        log.error "Invalid argument ${CURRENT_OPTION}"
        exit ${EXIT_INVALID_ARG}
        ;;        
    esac
  done    

  if [ -z "${KAMEHOUSE_USER}" ]; then
    log.error "Option -u is required"
    printHelpMenu
    exit ${EXIT_INVALID_ARG}
  fi
}

printHelpMenu() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-t (tag)${COL_NORMAL} docker image tag"
  echo -e "     ${COL_BLUE}-u (username)${COL_NORMAL} user running kamehouse [${COL_RED}required${COL_NORMAL}]"
}

main "$@"
