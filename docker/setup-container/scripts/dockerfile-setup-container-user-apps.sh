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
KAMEHOUSE_PASSWORD=""
IS_OUTSIDE_DOCKERFILE=false

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  parseArguments "$@"
  deleteDefaultUser
  setupKameHouseUser
  setupHttpd
  setupKameHouseUserHome
  setupTomcat
  setupMaven
  setupMockedBins
  setupRootBashRc
  fixPermissions
}

deleteDefaultUser() {
  log.info "Deleting default users"
  deluser -rf ubuntu 
}

setupKameHouseUser() {
  log.info "Setting up kamehouse user"
  adduser --gecos "" --disabled-password ${KAMEHOUSE_USER} 
  echo "${KAMEHOUSE_USER}:${KAMEHOUSE_PASSWORD}" | chpasswd 
  usermod -a -G adm ${KAMEHOUSE_USER} 
  usermod -a -G sudo ${KAMEHOUSE_USER} 
  touch /home/${KAMEHOUSE_USER}/.bashrc
  touch /home/${KAMEHOUSE_USER}/.profile
  echo ". /home/${KAMEHOUSE_USER}/.bashrc" > /home/${KAMEHOUSE_USER}/.profile
  chown ${KAMEHOUSE_USER}:users -R /home/${KAMEHOUSE_USER} 
}

setupHttpd() {
  log.info "Setting up httpd"
  mkdir -p /etc/apache2/conf
  cp -r /home/${KAMEHOUSE_USER}/docker/setup-container/apache2/conf/* /etc/apache2/conf
  cp -r /home/${KAMEHOUSE_USER}/docker/setup-container/apache2/sites-available/* /etc/apache2/sites-available
  cp /home/${KAMEHOUSE_USER}/docker/setup-container/apache2/certs/apache-selfsigned.crt /etc/ssl/certs/
  cp /home/${KAMEHOUSE_USER}/docker/setup-container/apache2/certs/apache-selfsigned.key /etc/ssl/private/
  cp /home/${KAMEHOUSE_USER}/docker/setup-container/apache2/robots.txt /var/www/html/
  chown ${KAMEHOUSE_USER}:users -R /var/www/html 

  ln -s /var/www/html/ /var/www/kamehouse-webserver 
  chown ${KAMEHOUSE_USER}:users -R /var/www/kamehouse-webserver 

  a2ensite default-ssl 
  a2enmod headers proxy proxy_http proxy_wstunnel ssl rewrite 

  mkdir -p /home/${KAMEHOUSE_USER}/programs/apache-httpd
  fixPermissions

  chmod a+rx /var/log/apache2 
  ln -s /var/log/apache2 /home/${KAMEHOUSE_USER}/programs/apache-httpd/logs   

  chown -R ${KAMEHOUSE_USER}:${KAMEHOUSE_USER} /var/log/apache2
  chown -R ${KAMEHOUSE_USER}:${KAMEHOUSE_USER} /var/lock/apache2
  chown -R ${KAMEHOUSE_USER}:${KAMEHOUSE_USER} /var/lib/apache2
  chown -R ${KAMEHOUSE_USER}:${KAMEHOUSE_USER} /var/lib/php/sessions
  sed -i "s#APACHE_RUN_USER=www-data#APACHE_RUN_USER=${KAMEHOUSE_USER}#g" /etc/apache2/envvars
  sed -i "s#APACHE_RUN_GROUP=www-data#APACHE_RUN_GROUP=${KAMEHOUSE_USER}#g" /etc/apache2/envvars

  rm /var/www/html/index.html 
}

setupKameHouseUserHome() {
  log.info "Setting up kamehouse user home"
  touch /home/${KAMEHOUSE_USER}/.env 
  echo "source /home/${KAMEHOUSE_USER}/.env" >> /home/${KAMEHOUSE_USER}/.bashrc

  mkdir -p /home/${KAMEHOUSE_USER}/.kamehouse/config/ 
  touch /home/${KAMEHOUSE_USER}/.kamehouse/config/.kamehouse-docker-container-env 
  echo "source /home/${KAMEHOUSE_USER}/.kamehouse/config/.kamehouse-docker-container-env" >> /home/${KAMEHOUSE_USER}/.bashrc

  mkdir -p /home/${KAMEHOUSE_USER}/.ssh 

  mkdir -p /home/${KAMEHOUSE_USER}/.config/vlc/ 
  cp -r /home/${KAMEHOUSE_USER}/docker/setup-container/vlc/* /home/${KAMEHOUSE_USER}/.config/vlc/

  mkdir -p /home/${KAMEHOUSE_USER}/programs/kamehouse-cmd/bin
  mkdir -p /home/${KAMEHOUSE_USER}/programs/kamehouse-cmd/lib
  fixPermissions
}

setupTomcat() {
  log.info "Setting up tomcat"
  sed -i "s#localhost:8000#0.0.0.0:8000#g" /home/${KAMEHOUSE_USER}/programs/apache-tomcat/bin/catalina.sh
  mkdir -p /home/${KAMEHOUSE_USER}/programs/apache-tomcat/conf/Catalina/localhost
  cp /home/${KAMEHOUSE_USER}/docker/setup-container/tomcat/server.xml /home/${KAMEHOUSE_USER}/programs/apache-tomcat/conf/
  cp /home/${KAMEHOUSE_USER}/docker/setup-container/tomcat/tomcat-users.xml /home/${KAMEHOUSE_USER}/programs/apache-tomcat/conf/
  cp /home/${KAMEHOUSE_USER}/docker/setup-container/tomcat/manager.xml /home/${KAMEHOUSE_USER}/programs/apache-tomcat/conf/Catalina/localhost/
  cp /home/${KAMEHOUSE_USER}/docker/setup-container/tomcat/host-manager.xml /home/${KAMEHOUSE_USER}/programs/apache-tomcat/conf/Catalina/localhost/
  fixPermissions
}

setupMaven() {
  log.info "Setting up maven"
  echo PATH=/home/${KAMEHOUSE_USER}/programs/apache-maven/bin:\${PATH} >> /home/${KAMEHOUSE_USER}/.bashrc
  mkdir -p /home/${KAMEHOUSE_USER}/programs/apache-maven/conf
  cp /home/${KAMEHOUSE_USER}/docker/setup-container/maven/settings.xml /home/${KAMEHOUSE_USER}/programs/apache-maven/conf/settings.xml
  echo "PATH=/home/${KAMEHOUSE_USER}/programs/apache-maven/bin:${PATH}" >> /etc/profile
  fixPermissions
}

setupMockedBins() {
  if ${IS_OUTSIDE_DOCKERFILE}; then
    return
  fi
  log.info "Setting up mocked bins"
  mv /usr/bin/vlc /usr/bin/vlc-bin 

  cp /home/${KAMEHOUSE_USER}/docker/setup-container/mocked-bin/vlc /usr/bin/vlc
  chmod a+rx /usr/bin/vlc 

  cp /home/${KAMEHOUSE_USER}/docker/setup-container/mocked-bin/gnome-screensaver-command /usr/bin/gnome-screensaver-command
  chmod a+rx /usr/bin/gnome-screensaver-command
}

setupRootBashRc() {
  log.info "Setting up root bashrc"
  # Setup bash prompt colors
  sed -i "s/#force_color_prompt=yes/force_color_prompt=yes/I" /root/.bashrc 
  sed -i "s/01;32m/01;31m/I" /root/.bashrc
}

fixPermissions() {
  log.info "Fixing permissions"
  chown ${KAMEHOUSE_USER}:users -R /home/${KAMEHOUSE_USER}/  
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
      --is-outside-dockerfile)
        IS_OUTSIDE_DOCKERFILE=true
        ;;
      -u)
        KAMEHOUSE_USER="${CURRENT_OPTION_ARG}"
        ;;
      -p)
        KAMEHOUSE_PASSWORD="${CURRENT_OPTION_ARG}"
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

  if [ -z "${KAMEHOUSE_PASSWORD}" ]; then
    log.error "Option -p is required"
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
  echo -e "     ${COL_BLUE}-u (username)${COL_NORMAL} user running kamehouse [${COL_RED}required${COL_NORMAL}]"
  echo -e "     ${COL_BLUE}-p (password)${COL_NORMAL} password for user running kamehouse [${COL_RED}required${COL_NORMAL}]"
  echo -e "     ${COL_BLUE}--is-outside-dockerfile${COL_NORMAL} Set this flag when running outside dockerfile"
}

main "$@"
