#!/bin/bash
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
USERNAME=nbrest

main() {
  echo -e "${COL_CYAN}*********************************************************************************${COL_NORMAL}"
  echo -e "${COL_CYAN}    ${KAMEHOUSE}${COL_CYAN} docker init script${COL_NORMAL}"
  echo -e "${COL_CYAN}*********************************************************************************${COL_NORMAL}"
  loadEnv
  pullKameHouse
  deployKamehouse
  startTomcat
  restartSshService
  startMysql
  startHttpd
  keepContainerAlive
}

loadEnv() {
  logStep "Loading container environment"
  source /root/.bashrc
  IS_DOCKER_CONTAINER=true
  
  if [ -z "${FAST_DOCKER_INIT}" ]; then
    # by default don't do fast init. can set the environment FAST_DOCKER_INIT=true when running the container
    logStep "Setting default FAST_DOCKER_INIT=false"
    FAST_DOCKER_INIT=false
  fi

  echo ""
  logStep "FAST_DOCKER_INIT=${FAST_DOCKER_INIT}"
  logStep "PERSISTENT_DATA=${PERSISTENT_DATA}"
  logStep "DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST}"
  logStep "DOCKER_HOST_IP=${DOCKER_HOST_IP}"
  logStep "DOCKER_HOST_OS=${DOCKER_HOST_OS}"
  logStep "DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME}"
  logStep "IS_DOCKER_CONTAINER=${IS_DOCKER_CONTAINER}"
  echo ""

  local CONTAINER_ENV=/home/nbrest/.kamehouse-docker-container-env
  echo "# Environment status at container startup on `date`" > ${CONTAINER_ENV}
  echo "FAST_DOCKER_INIT=${FAST_DOCKER_INIT}" >> ${CONTAINER_ENV}
  echo "PERSISTENT_DATA=${PERSISTENT_DATA}" >> ${CONTAINER_ENV}
  echo "DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_IP=${DOCKER_HOST_IP}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_OS=${DOCKER_HOST_OS}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME}" >> ${CONTAINER_ENV}
  echo "IS_DOCKER_CONTAINER=${IS_DOCKER_CONTAINER}" >> ${CONTAINER_ENV}
  chown nbrest:nbrest ${CONTAINER_ENV}
}

pullKameHouse() {
  if [ "${FAST_DOCKER_INIT}" == "false" ]; then
    logStep "Pulling latest KameHouse dev branch"
    sudo su - ${USERNAME} -c "cd /home/nbrest/git/java.web.kamehouse ; git pull origin dev"
  fi
}

deployKamehouse() {
  if [ "${FAST_DOCKER_INIT}" == "false" ]; then
    logStep "Deploying latest version of KameHouse"
    sudo su - ${USERNAME} -c "/home/nbrest/my.scripts/kamehouse/deploy-java-web-kamehouse.sh -f -p docker"
    sudo su - ${USERNAME} -c "/home/nbrest/my.scripts/kamehouse/docker/docker-my-scripts-update.sh"
    logStep "Finished building latest version of KameHouse"
  fi
}

startTomcat() {
  logStep "Starting tomcat"
  sudo su - ${USERNAME} -c "cd /home/nbrest/programs/apache-tomcat ; \
  USER_UID=`sudo cat /etc/passwd | grep ${USERNAME} | cut -d ':' -f3` ; \
  DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/${USER_UID}/bus DISPLAY=:0.0 bin/startup.sh"
}

restartSshService() {
  logStep "Restarting ssh service"
  service ssh restart
}

startMysql() {
  logStep "Starting mysql"
  service mysql start
}

startHttpd() {
  logStep "Starting apache httpd"
  rm /var/run/apache2/apache2.pid 2>/dev/null
  service apache2 start
}

keepContainerAlive() {
  echo -e "${COL_RED}*********************************************************************************${COL_NORMAL}"
  echo ""
  echo -e "   ${KAMEHOUSE} ${COL_NORMAL}docker init script ${COL_RED}finished${COL_NORMAL}"
  echo ""
  echo -e "${COL_BLUE} - ${COL_NORMAL}Open another terminal and execute ${COL_PURPLE}'tail-log.sh -f tomcat'${COL_NORMAL} to check the logs"
  echo -e "${COL_NORMAL} until the deployment finishes"
  echo ""
  echo -e "${COL_BLUE} - ${COL_NORMAL}Check ${COL_BLUE}https://github.com/nbrest/java.web.kamehouse/blob/dev/docker-setup.md${COL_NORMAL}"
  echo -e " for details on how to login to kamehouse and execute its functionality" 
  echo ""
  echo -e "${COL_RED}*********************************************************************************${COL_NORMAL}"
  echo -e "${COL_RED}         Keep this terminal open while the container is running${COL_NORMAL}"
  echo -e "${COL_RED}*********************************************************************************${COL_NORMAL}"

  echo "" > /root/.docker-init-script.lock
  tail -f /root/.docker-init-script.lock
  read 
}

logStep() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"