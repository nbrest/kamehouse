#!/bin/bash
# This script runs inside the docker container, not on the host
# Init script to execute every time a docker instance starts

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

KAMEHOUSE=${COL_NORMAL}Kame${COL_RED}House${COL_MESSAGE}
DOCKER_CONTAINER_USERNAME=`ls /home`

main() {
  echo -e "${COL_CYAN}*********************************************************************************${COL_NORMAL}"
  echo -e "${COL_CYAN}    ${KAMEHOUSE}${COL_CYAN} docker init script${COL_NORMAL}"
  echo -e "${COL_CYAN}*********************************************************************************${COL_NORMAL}"
  loadEnv
  restartSshService
  startHttpd
  startMariadb
  if [ "${BUILD_ON_STARTUP}" == "true" ]; then
    pullKameHouse
    deployKameHouse
    startTomcat
  else
    checkKameHouseWar
    startTomcat
  fi
  configGitDevDir
  printEnv
  printStartupMessage
  keepContainerAlive
}

loadEnv() {
  log.info "Loading container environment"
  source /root/.bashrc
  IS_DOCKER_CONTAINER=true
  if [ -z "${BUILD_ON_STARTUP}" ]; then
    # by default do fast init. can set the environment BUILD_ON_STARTUP=false when running the container
    log.info "Setting default BUILD_ON_STARTUP=false"
    BUILD_ON_STARTUP=false
  fi
  printEnv

  local CONTAINER_ENV=/home/${DOCKER_CONTAINER_USERNAME}/.kamehouse/config/.kamehouse-docker-container-env
  echo "# Environment status at container startup on `date`" > ${CONTAINER_ENV}
  echo "BUILD_ON_STARTUP=${BUILD_ON_STARTUP}" >> ${CONTAINER_ENV}
  echo "DEBUG_MODE=${DEBUG_MODE}" >> ${CONTAINER_ENV}
  echo "DOCKER_BASE_OS=${DOCKER_BASE_OS}" >> ${CONTAINER_ENV}
  echo "DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_AUTH=${DOCKER_HOST_AUTH}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_IP=${DOCKER_HOST_IP}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_HOSTNAME=${DOCKER_HOST_HOSTNAME}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_OS=${DOCKER_HOST_OS}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_PLAYLISTS_PATH=${DOCKER_HOST_PLAYLISTS_PATH}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_PORT=${DOCKER_HOST_PORT}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_MARIADB=${DOCKER_PORT_MARIADB}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_CMD_LINE_DEBUG=${DOCKER_PORT_CMD_LINE_DEBUG}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_SSH=${DOCKER_PORT_SSH}" >> ${CONTAINER_ENV}
  echo "IS_DOCKER_CONTAINER=${IS_DOCKER_CONTAINER}" >> ${CONTAINER_ENV}
  echo "IS_LINUX_DOCKER_HOST=${IS_LINUX_DOCKER_HOST}" >> ${CONTAINER_ENV}
  echo "DOCKER_PROFILE=${DOCKER_PROFILE}" >> ${CONTAINER_ENV}
  echo "USE_VOLUMES=${USE_VOLUMES}" >> ${CONTAINER_ENV}
  chown ${DOCKER_CONTAINER_USERNAME}:${DOCKER_CONTAINER_USERNAME} ${CONTAINER_ENV}
  chmod go-rwx ${CONTAINER_ENV}
}

printEnv() {
  log.info "Container environment:"
  echo ""
  log.info "BUILD_ON_STARTUP=${BUILD_ON_STARTUP}"
  log.info "DEBUG_MODE=${DEBUG_MODE}"
  log.info "DOCKER_BASE_OS=${DOCKER_BASE_OS}"
  log.info "DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST}"
  log.info "DOCKER_HOST_AUTH=****"
  log.info "DOCKER_HOST_IP=${DOCKER_HOST_IP}"
  log.info "DOCKER_HOST_HOSTNAME=${DOCKER_HOST_HOSTNAME}"
  log.info "DOCKER_HOST_OS=${DOCKER_HOST_OS}"
  log.info "DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME}"
  log.info "DOCKER_HOST_PLAYLISTS_PATH=${DOCKER_HOST_PLAYLISTS_PATH}"
  log.info "DOCKER_HOST_PORT=${DOCKER_HOST_PORT}"
  log.info "DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP}"
  log.info "DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS}"
  log.info "DOCKER_PORT_MARIADB=${DOCKER_PORT_MARIADB}"
  log.info "DOCKER_PORT_SSH=${DOCKER_PORT_SSH}"
  log.info "DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG}"
  log.info "DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT}"
  log.info "DOCKER_PORT_CMD_LINE_DEBUG=${DOCKER_PORT_CMD_LINE_DEBUG}"
  log.info "IS_DOCKER_CONTAINER=${IS_DOCKER_CONTAINER}"
  log.info "IS_LINUX_DOCKER_HOST=${IS_LINUX_DOCKER_HOST}"
  log.info "DOCKER_PROFILE=${DOCKER_PROFILE}"
  log.info "USE_VOLUMES=${USE_VOLUMES}"
  echo ""
}

configGitDevDir() {
  if [ "${DOCKER_PROFILE}" == "dev" ]; then
    log.info "Configuring git dev directory"
    sudo su - ${DOCKER_CONTAINER_USERNAME} -c "cd /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse ; git config --global --add safe.directory /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse"
  fi
}

pullKameHouse() {
  log.info "Pulling latest KameHouse dev branch"
  sudo su - ${DOCKER_CONTAINER_USERNAME} -c "cd /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse ; git pull origin dev"
}

deployKameHouse() {
  log.info "Deploying latest version of KameHouse"
  sudo su - ${DOCKER_CONTAINER_USERNAME} -c "/home/${DOCKER_CONTAINER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/deploy/deploy-kamehouse.sh -p docker -m shell"
  sudo su - ${DOCKER_CONTAINER_USERNAME} -c "/home/${DOCKER_CONTAINER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/deploy/deploy-kamehouse.sh -p docker"
  log.info "Finished deploying latest version of KameHouse"
}

checkKameHouseWar() {
  local KAMEHOUSE_WAR="/home/${DOCKER_CONTAINER_USERNAME}/programs/apache-tomcat/webapps/kame-house.war"
  if [ ! -f "${KAMEHOUSE_WAR}" ]; then
    log.info "Looks like kamehouse webapps were not built at docker image creation. Deploying kamehouse before tomcat startup"
    deployKameHouse
  fi
}

startTomcat() {
  local START_TOMCAT_CMD="/home/${DOCKER_CONTAINER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/tomcat/tomcat-startup.sh"
  if ${DEBUG_MODE}; then
    log.info "Starting tomcat in debug mode"
    START_TOMCAT_CMD=${START_TOMCAT_CMD}" -d"
  else
    log.info "Starting tomcat"
  fi
  sudo su - ${DOCKER_CONTAINER_USERNAME} -c "${START_TOMCAT_CMD}"
}

restartSshService() {
  log.info "Restarting ssh service"
  service ssh restart
}

startMariadb() {
  log.info "Starting mariadb"
  service mariadb start
}

startHttpd() {
  log.info "Starting apache httpd"
  rm /var/run/apache2/apache2.pid 2>/dev/null
  service apache2 start
}

printStartupMessage() {
  echo -e "${COL_RED}*********************************************************************************${COL_NORMAL}"
  echo ""
  echo -e "   ${KAMEHOUSE} ${COL_NORMAL}docker container init script ${COL_RED}finished${COL_NORMAL}"
  echo ""

  echo -e "${COL_BLUE} - ${COL_NORMAL}Open a shell in the container from another terminal:"
  echo -e "     ${COL_PURPLE}docker-shell-kamehouse.sh -p ${DOCKER_PROFILE}${COL_NORMAL}"
  echo ""

  echo -e "${COL_BLUE} - ${COL_NORMAL}Or ssh into the container from another terminal:"
  echo -e "     ${COL_PURPLE}docker-ssh-kamehouse.sh -p ${DOCKER_PROFILE}${COL_NORMAL}"
  echo ""

  echo -e "${COL_BLUE} - ${COL_NORMAL}From the container's shell check the logs until the deployment finishes:"
  echo -e "     ${COL_PURPLE}tail-log.sh -f tomcat -n 2000${COL_NORMAL}"
  echo ""

  echo -e "${COL_BLUE} - ${COL_NORMAL}For details on how to login to kamehouse and execute its functionality open:" 
  echo -e "     ${COL_BLUE}https://github.com/nbrest/kamehouse/blob/dev/docs/docker/docker-setup.md${COL_NORMAL}"
  echo ""

  echo -e "${COL_BLUE} - ${COL_NORMAL}Enter KameHouse at: ${COL_BLUE}http://localhost:${DOCKER_PORT_HTTP}/kame-house/${COL_NORMAL}"
  echo ""

  echo -e "${COL_BLUE} - ${COL_NORMAL}Stop the container from another terminal:"
  echo -e "     ${COL_PURPLE}docker-stop-kamehouse.sh -p ${DOCKER_PROFILE}${COL_NORMAL}"
  echo ""

  echo -e "${COL_RED}*********************************************************************************${COL_NORMAL}"
  echo -e "${COL_RED}         Keep this terminal open while the container is running${COL_NORMAL}"
  echo -e "${COL_RED}*********************************************************************************${COL_NORMAL}"
}

keepContainerAlive() {
  echo "" > /root/.docker-init-script.lock
  tail -f /root/.docker-init-script.lock
  read 
}

log.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_CYAN_STD}${SCRIPT_NAME}${COL_NORMAL} - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"
