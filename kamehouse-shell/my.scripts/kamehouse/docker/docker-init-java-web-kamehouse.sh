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
  echo -e "${COL_CYAN}*********************************************************${COL_NORMAL}"
  echo -e "${COL_CYAN} ${KAMEHOUSE}${COL_CYAN} docker init script${COL_NORMAL}"
  echo -e "${COL_CYAN}*********************************************************${COL_NORMAL}"  
  loadEnv
  pullKameHouse
  startTomcat
  restartSshService
  startMysql
  startHttpd
  deployKamehouse
  keepContainerAlive
}

loadEnv() {
  logStep "Load env"
  source /root/.bashrc

  if [ -z "${PULL_KAMEHOUSE}" ]; then
    # by default pull. can set the environment PULL_KAMEHOUSE=false when creating the container
    logStep "Setting default PULL_KAMEHOUSE=true"
    export PULL_KAMEHOUSE=true
  fi 

  if [ -z "${DEPLOY_KAMEHOUSE}" ]; then
    # by default deploy. can set the environment DEPLOY_KAMEHOUSE=false when creating the container
    logStep "Setting default DEPLOY_KAMEHOUSE=true"
    export DEPLOY_KAMEHOUSE=true
  fi 
}

pullKameHouse() {
  if ${PULL_KAMEHOUSE}; then
    logStep "Pull latest KameHouse dev branch"
    sudo su - ${USERNAME} -c "cd /home/nbrest/git/java.web.kamehouse ; git pull origin dev"
  fi
}

startTomcat() {
  logStep "Start tomcat"
  sudo su - ${USERNAME} -c "cd /home/nbrest/programs/apache-tomcat ; \
  USER_UID=`sudo cat /etc/passwd | grep ${USERNAME} | cut -d ':' -f3` ; \
  DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/${USER_UID}/bus DISPLAY=:0.0 bin/startup.sh"
}

restartSshService() {
  logStep "Restart ssh service"
  service ssh restart
}

startMysql() {
  logStep "Start mysql"
  service mysql start
}

startHttpd() {
  logStep "Start apache httpd"
  rm /var/run/apache2/apache2.pid
  service apache2 start
}

deployKamehouse() {
  if ${DEPLOY_KAMEHOUSE}; then
    logStep "Deploy KameHouse"
    sudo su - ${USERNAME} -c "/home/nbrest/my.scripts/kamehouse/deploy-java-web-kamehouse.sh -f -p docker"
    logStep "Finished building KameHouse"
  fi
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