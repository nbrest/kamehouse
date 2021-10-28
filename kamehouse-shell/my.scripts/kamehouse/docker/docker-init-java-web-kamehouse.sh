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
  logStep "Loading env"
  source /root/.bashrc

  if [ -z "${PULL_KAMEHOUSE}" ]; then
    # by default pull. can set the environment PULL_KAMEHOUSE=false when creating the container
    logStep "Setting default PULL_KAMEHOUSE=true"
    export PULL_KAMEHOUSE=true
  else
    logStep "PULL_KAMEHOUSE set to ${PULL_KAMEHOUSE}"
  fi 

  local CONTAINER_ENV=/home/nbrest/.container-env
  echo "# Environment status at container startup on `date`" > ${CONTAINER_ENV}
  echo "PULL_KAMEHOUSE=${PULL_KAMEHOUSE}" >> ${CONTAINER_ENV}
  echo "KAMEHOUSE_HOST_IP=${KAMEHOUSE_HOST_IP}" >> ${CONTAINER_ENV}
  chown nbrest:nbrest ${CONTAINER_ENV}
}

pullKameHouse() {
  if ${PULL_KAMEHOUSE}; then
    logStep "Pulling latest KameHouse dev branch"
    sudo su - ${USERNAME} -c "cd /home/nbrest/git/java.web.kamehouse ; git pull origin dev"
  fi
}

deployKamehouse() {
  if ${PULL_KAMEHOUSE}; then
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

  echo "" > /root/.startup.lock
  tail -f /root/.startup.lock
  read
}

logStep() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"