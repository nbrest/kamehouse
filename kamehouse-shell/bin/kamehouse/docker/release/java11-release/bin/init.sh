#!/bin/bash
# This script runs inside the docker container, not on the host
# Init script to execute every time a docker instance starts

COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_MESSAGE=${COL_GREEN}

KAMEHOUSE=${COL_NORMAL}Kame${COL_RED}House${COL_MESSAGE}

DOCKER_CONTAINER_USERNAME=`ls /home | grep -v "nbrest"`

. /home/${DOCKER_CONTAINER_USERNAME}/.env

main() {
  echo -e "${COL_CYAN}*********************************************************************************${COL_NORMAL}"
  echo -e "${COL_CYAN}    ${KAMEHOUSE}${COL_CYAN} docker init script${COL_NORMAL}"
  echo -e "${COL_CYAN}*********************************************************************************${COL_NORMAL}"
  restartSshService
  startHttpd
  startMysql
  startTomcat
  fixEolMyScripts
  fixGitConfig
  keepContainerAlive
}

startHttpd() {
  log.info "Starting apache httpd"
  rm /var/run/apache2/apache2.pid 2>/dev/null
  service apache2 start
}

restartSshService() {
  log.info "Restarting ssh service"
  service ssh restart
}

startMysql() {
  log.info "Starting mysql/mariadb"
  service mariadb start
}

startTomcat() {
  local START_TOMCAT_CMD="export USER_UID=`cat /etc/passwd | grep "${DOCKER_CONTAINER_USERNAME}" | cut -d ':' -f3` ; \
    export DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/${USER_UID}/bus \
    export DISPLAY=:0.0 ; \
    cd /home/${DOCKER_CONTAINER_USERNAME}/programs/apache-tomcat ; \
    ./bin/startup.sh"

  log.info "Starting tomcat"
  sudo su - ${DOCKER_CONTAINER_USERNAME} -c "${START_TOMCAT_CMD}"
}

fixEolMyScripts() {
  log.info "Fixing eol on my.scripts"
  cd /home/${DOCKER_CONTAINER_USERNAME}/my.scripts
  /home/${DOCKER_CONTAINER_USERNAME}/bin/fix-eol.sh 2>&1 /dev/null
}

fixGitConfig() {
  log.info "Fixing git config"
  git config --global --add safe.directory /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse
  sudo -u ${DOCKER_CONTAINER_USERNAME} git config --global --add safe.directory /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse  
  sudo -u nbrest git config --global --add safe.directory /home/nbrest/git/kamehouse
  sudo -u nbrest git config --global --add safe.directory /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse

  git config --global --add safe.directory /home/${DOCKER_CONTAINER_USERNAME}/my.scripts
  sudo -u ${DOCKER_CONTAINER_USERNAME} git config --global --add safe.directory /home/${DOCKER_CONTAINER_USERNAME}/my.scripts  
  sudo -u nbrest git config --global --add safe.directory /home/nbrest/my.scripts
  sudo -u nbrest git config --global --add safe.directory /home/${DOCKER_CONTAINER_USERNAME}/my.scripts
}

keepContainerAlive() {
  echo -e "${COL_RED}*********************************************************************************${COL_NORMAL}"
  echo ""
  echo -e "   ${KAMEHOUSE} ${COL_NORMAL}docker init script ${COL_RED}finished${COL_NORMAL}"
  echo ""
  echo -e "${COL_RED}*********************************************************************************${COL_NORMAL}"
  echo -e "${COL_RED}         Keep this terminal open while the container is running${COL_NORMAL}"
  echo -e "${COL_RED}*********************************************************************************${COL_NORMAL}"

  echo "" > /root/.docker-init-script.lock
  tail -f /root/.docker-init-script.lock
  read 
}

log.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"
