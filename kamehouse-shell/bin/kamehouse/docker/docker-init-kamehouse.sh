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
DOCKER_CONTAINER_USERNAME=`ls /home`

main() {
  echo -e "${COL_CYAN}*********************************************************************************${COL_NORMAL}"
  echo -e "${COL_CYAN}    ${KAMEHOUSE}${COL_CYAN} docker init script${COL_NORMAL}"
  echo -e "${COL_CYAN}*********************************************************************************${COL_NORMAL}"
  loadEnv
  restartSshService
  startHttpd
  startMysql
  pullKameHouse
  deployKameHouse
  startTomcat
  printEnv
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
  findHostIpAddress
  printEnv

  local CONTAINER_ENV=/home/${DOCKER_CONTAINER_USERNAME}/.kamehouse/.kamehouse-docker-container-env
  echo "# Environment status at container startup on `date`" > ${CONTAINER_ENV}
  echo "BUILD_ON_STARTUP=${BUILD_ON_STARTUP}" >> ${CONTAINER_ENV}
  echo "DEBUG_MODE=${DEBUG_MODE}" >> ${CONTAINER_ENV}
  echo "DOCKER_BASE_OS=${DOCKER_BASE_OS}" >> ${CONTAINER_ENV}
  echo "DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_IP=${DOCKER_HOST_IP}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_HOSTNAME=${DOCKER_HOST_HOSTNAME}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_OS=${DOCKER_HOST_OS}" >> ${CONTAINER_ENV}
  echo "DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_MYSQL=${DOCKER_PORT_MYSQL}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT}" >> ${CONTAINER_ENV}
  echo "DOCKER_PORT_SSH=${DOCKER_PORT_SSH}" >> ${CONTAINER_ENV}
  echo "EXPORT_NATIVE_HTTPD=${EXPORT_NATIVE_HTTPD}" >> ${CONTAINER_ENV}
  echo "IS_DOCKER_CONTAINER=${IS_DOCKER_CONTAINER}" >> ${CONTAINER_ENV}
  echo "IS_LINUX_DOCKER_HOST=${IS_LINUX_DOCKER_HOST}" >> ${CONTAINER_ENV}
  echo "PROFILE=${PROFILE}" >> ${CONTAINER_ENV}
  echo "USE_VOLUMES=${USE_VOLUMES}" >> ${CONTAINER_ENV}
  chown ${DOCKER_CONTAINER_USERNAME}:${DOCKER_CONTAINER_USERNAME} ${CONTAINER_ENV}
}

printEnv() {
  log.info "Container environment:"
  echo ""
  log.info "BUILD_ON_STARTUP=${BUILD_ON_STARTUP}"
  log.info "DEBUG_MODE=${DEBUG_MODE}"
  log.info "DOCKER_BASE_OS=${DOCKER_BASE_OS}"
  log.info "DOCKER_CONTROL_HOST=${DOCKER_CONTROL_HOST}"
  log.info "DOCKER_HOST_IP=${DOCKER_HOST_IP}"
  log.info "DOCKER_HOST_HOSTNAME=${DOCKER_HOST_HOSTNAME}"
  log.info "DOCKER_HOST_OS=${DOCKER_HOST_OS}"
  log.info "DOCKER_HOST_USERNAME=${DOCKER_HOST_USERNAME}"
  log.info "DOCKER_PORT_HTTP=${DOCKER_PORT_HTTP}"
  log.info "DOCKER_PORT_HTTPS=${DOCKER_PORT_HTTPS}"
  log.info "DOCKER_PORT_MYSQL=${DOCKER_PORT_MYSQL}"
  log.info "DOCKER_PORT_SSH=${DOCKER_PORT_SSH}"
  log.info "DOCKER_PORT_TOMCAT_DEBUG=${DOCKER_PORT_TOMCAT_DEBUG}"
  log.info "DOCKER_PORT_TOMCAT=${DOCKER_PORT_TOMCAT}"
  log.info "EXPORT_NATIVE_HTTPD=${EXPORT_NATIVE_HTTPD}"
  log.info "IS_DOCKER_CONTAINER=${IS_DOCKER_CONTAINER}"
  log.info "IS_LINUX_DOCKER_HOST=${IS_LINUX_DOCKER_HOST}"
  log.info "PROFILE=${PROFILE}"
  log.info "USE_VOLUMES=${USE_VOLUMES}"
  echo ""
}

findHostIpAddress() {
  if [ -z "${DOCKER_HOST_IP}" ]; then
    log.info "Host IP not set by docker run script. Attempting to find it now"
    local IP=`ifconfig | grep inet | grep -v 127.0.0.1 | awk '{print $2}'`
    local IP_SPLIT=(${IP//./ })
    local IP_LAST=`echo ${IP_SPLIT[3]}`
    local IP_LAST_HOST=`echo "$(($IP_LAST-1))"`
    local IP_HOST=`echo "${IP_SPLIT[0]}.${IP_SPLIT[1]}.${IP_SPLIT[2]}.${IP_LAST_HOST}"`
    ping -c 1 $IP_HOST >/dev/null
    local RESULT=`echo $?`
    if [ "$RESULT" == "0" ]; then
       log.info "Host IP assigned successfully to ${IP_HOST}"
       DOCKER_HOST_IP=${IP_HOST}
    else
      log.info "ERROR!! Unable to set host IP address"
    fi
  fi
}

pullKameHouse() {
  if [ "${BUILD_ON_STARTUP}" == "true" ]; then
    log.info "Pulling latest KameHouse dev branch"
    sudo su - ${DOCKER_CONTAINER_USERNAME} -c "cd /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse ; git pull origin dev"
  fi
}

deployKameHouse() {
  if [ "${BUILD_ON_STARTUP}" == "true" ]; then
    log.info "Deploying latest version of KameHouse"
    sudo su - ${DOCKER_CONTAINER_USERNAME} -c "/home/${DOCKER_CONTAINER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/deploy-kamehouse.sh -f -p docker"
    log.info "Finished building latest version of KameHouse"
  fi
}

startTomcat() {
  local START_TOMCAT_CMD="export USER_UID=`sudo cat /etc/passwd | grep ${DOCKER_CONTAINER_USERNAME} | cut -d ':' -f3` ; \
    export DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/${USER_UID}/bus \
    export DISPLAY=:0.0 ; \
    cd /home/${DOCKER_CONTAINER_USERNAME}/programs/apache-tomcat ; \
    /home/${DOCKER_CONTAINER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/tomcat-startup.sh"

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

startMysql() {
  log.info "Starting mysql"
  service mysql start
  /home/${DOCKER_CONTAINER_USERNAME}/programs/kamehouse-shell/bin/common/mysql/add-mysql-user-nikolqs.sh > /home/${DOCKER_CONTAINER_USERNAME}/logs/add-mysql-user-nikolqs.log
  chown ${DOCKER_CONTAINER_USERNAME}:users /home/${DOCKER_CONTAINER_USERNAME}/logs/add-mysql-user-nikolqs.log
}

startHttpd() {
  log.info "Starting apache httpd"
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
  echo -e "${COL_BLUE} - ${COL_NORMAL}Check ${COL_BLUE}https://github.com/nbrest/kamehouse/blob/dev/docker-setup.md${COL_NORMAL}"
  echo -e " for details on how to login to kamehouse and execute its functionality" 
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
