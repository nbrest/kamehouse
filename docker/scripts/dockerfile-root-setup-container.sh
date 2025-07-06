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

# When updating versions here, also update in /docs/versions/versions.md
MAVEN_TOP_LEVEL_VERSION=3
MAVEN_VERSION=3.9.3
TOMCAT_TOP_LEVEL_VERSION=10
TOMCAT_VERSION=10.1.11

KAMEHOUSE_USER=""
KAMEHOUSE_PASSWORD=""

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

main() {
  parseArguments "$@"
  setupUser
  setupHttpd
  setupUserHome
  installTomcat
  installMaven
  setupMockedBins
  setupRootBashRc
  fixPermissions
}

setupUser() {
  log.info "Setting up kamehouse user"
  adduser --gecos "" --disabled-password ${KAMEHOUSE_USER} 
  echo "${KAMEHOUSE_USER}:${KAMEHOUSE_PASSWORD}" | chpasswd 
  usermod -a -G adm ${KAMEHOUSE_USER} 
  usermod -a -G sudo ${KAMEHOUSE_USER} 
  chown ${KAMEHOUSE_USER}:users -R /home/${KAMEHOUSE_USER} 
}

setupHttpd() {
  log.info "Setting up httpd"
  cp -r /home/${KAMEHOUSE_USER}/docker/apache2/conf /etc/apache2/conf
  cp -r /home/${KAMEHOUSE_USER}/docker/apache2/sites-available /etc/apache2/sites-available
  cp /home/${KAMEHOUSE_USER}/docker/apache2/certs/apache-selfsigned.crt /etc/ssl/certs/
  cp /home/${KAMEHOUSE_USER}/docker/apache2/certs/apache-selfsigned.key /etc/ssl/private/
  cp /home/${KAMEHOUSE_USER}/docker/apache2/robots.txt /var/www/html/
  chown ${KAMEHOUSE_USER}:users -R /var/www/html 

  ln -s /var/www/html/ /var/www/kamehouse-webserver 
  chown ${KAMEHOUSE_USER}:users -R /var/www/kamehouse-webserver 

  a2ensite default-ssl 
  a2enmod headers proxy proxy_http proxy_wstunnel ssl rewrite 

  sudo su - ${KAMEHOUSE_USER} -c "mkdir -p /home/${KAMEHOUSE_USER}/programs/apache-httpd"

  chmod a+rx /var/log/apache2 
  ln -s /var/log/apache2 /home/${KAMEHOUSE_USER}/programs/apache-httpd/logs   

  rm /var/www/html/index.html 
}

setupUserHome() {
  log.info "Setting up kamehouse user home"
  sudo su - ${KAMEHOUSE_USER} -c "echo \"source /home/${KAMEHOUSE_USER}/.kamehouse/config/.kamehouse-docker-container-env\" >> /home/${KAMEHOUSE_USER}/.bashrc ; \
    mkdir -p /home/${KAMEHOUSE_USER}/.ssh" 

  sudo su - ${KAMEHOUSE_USER} -c "mkdir -p /home/${KAMEHOUSE_USER}/.config/vlc/" 
  cp -r /home/${KAMEHOUSE_USER}/docker/vlc/* /home/${KAMEHOUSE_USER}/.config/vlc/

  sudo su - ${KAMEHOUSE_USER} -c "mkdir -p /home/${KAMEHOUSE_USER}/programs/kamehouse-cmd/bin ; \
  mkdir -p /home/${KAMEHOUSE_USER}/programs/kamehouse-cmd/lib"
}

installTomcat() {
  log.info "Setting up tomcat"
  sudo su - ${KAMEHOUSE_USER} -c "mkdir -p /home/${KAMEHOUSE_USER}/programs ; \
  cd /home/${KAMEHOUSE_USER}/programs ; \
  wget --no-check-certificate https://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_TOP_LEVEL_VERSION}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz ; \
  tar -xf /home/${KAMEHOUSE_USER}/programs/apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /home/${KAMEHOUSE_USER}/programs/ ; \
  mv /home/${KAMEHOUSE_USER}/programs/apache-tomcat-${TOMCAT_VERSION} /home/${KAMEHOUSE_USER}/programs/apache-tomcat ; \
  rm /home/${KAMEHOUSE_USER}/programs/apache-tomcat-${TOMCAT_VERSION}.tar.gz ; \
  sed -i \"s#localhost:8000#0.0.0.0:8000#g\" /home/${KAMEHOUSE_USER}/programs/apache-tomcat/bin/catalina.sh" 

  cp /home/${KAMEHOUSE_USER}/docker/tomcat/server.xml /home/${KAMEHOUSE_USER}/programs/apache-tomcat/conf/
  cp /home/${KAMEHOUSE_USER}/docker/tomcat/tomcat-users.xml /home/${KAMEHOUSE_USER}/programs/apache-tomcat/conf/
  cp /home/${KAMEHOUSE_USER}/docker/tomcat/manager.xml /home/${KAMEHOUSE_USER}/programs/apache-tomcat/conf/Catalina/localhost/
  cp /home/${KAMEHOUSE_USER}/docker/tomcat/host-manager.xml /home/${KAMEHOUSE_USER}/programs/apache-tomcat/conf/Catalina/localhost/
  cp /home/${KAMEHOUSE_USER}/docker/maven/settings.xml /home/${KAMEHOUSE_USER}/programs/apache-maven/conf/settings.xml  
}

installMaven() {
  log.info "Setting up maven"
  sudo su - ${KAMEHOUSE_USER} -c "cd /home/${KAMEHOUSE_USER}/programs 
  wget --no-check-certificate https://archive.apache.org/dist/maven/maven-${MAVEN_TOP_LEVEL_VERSION}/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz ; \
  tar -xf /home/${KAMEHOUSE_USER}/programs/apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /home/${KAMEHOUSE_USER}/programs/ ; \
  mv /home/${KAMEHOUSE_USER}/programs/apache-maven-${MAVEN_VERSION} /home/${KAMEHOUSE_USER}/programs/apache-maven ; \
  rm /home/${KAMEHOUSE_USER}/programs/apache-maven-${MAVEN_VERSION}-bin.tar.gz ; \
  echo PATH=/home/${KAMEHOUSE_USER}/programs/apache-maven/bin:\${PATH} >> /home/${KAMEHOUSE_USER}/.bashrc ; \
  echo . /home/${KAMEHOUSE_USER}/.env >> /home/${KAMEHOUSE_USER}/.bashrc" 

  echo "PATH=/home/${KAMEHOUSE_USER}/programs/apache-maven/bin:${PATH}" >> /etc/profile ; \
}

setupMockedBins() {
  log.info "Setting up mocked bins"
  mv /usr/bin/vlc /usr/bin/vlc-bin 

  cp /home/${KAMEHOUSE_USER}/docker/mocked-bin/vlc /usr/bin/vlc
  chmod a+rx /usr/bin/vlc 

  cp /home/${KAMEHOUSE_USER}/docker/mocked-bin/gnome-screensaver-command /usr/bin/gnome-screensaver-command
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
  echo -e "Usage: ${COL_PURPLE}dockerfile-root-setup-container.sh${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-u (username)${COL_NORMAL} user running kamehouse [${COL_RED}required${COL_NORMAL}]"
  echo -e "     ${COL_BLUE}-p (password)${COL_NORMAL} password for user running kamehouse [${COL_RED}required${COL_NORMAL}]"
}

main "$@"
