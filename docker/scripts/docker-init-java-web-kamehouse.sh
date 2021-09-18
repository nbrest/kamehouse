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

main() {
  echo -e "${COL_CYAN}*********************************************************${COL_NORMAL}"
  echo -e "${COL_CYAN} KameHouse docker init script${COL_NORMAL}"
  echo -e "${COL_CYAN}*********************************************************${COL_NORMAL}"  

  cloneKameHouse
  setupDirectories
  setupEnv
  setupMockedBins
  restartSshService
  startMysql
  initKameHouseDb
  startHttpd
  startTomcat
  deployKamehouse
  keepContainerAlive
}

cloneKameHouse() {
  logStep "Clone latest KameHouse dev branch"
  mkdir -p /root/git
  cd /root/git
  rm -rf /root/git/java.web.kamehouse 
  git clone https://github.com/nbrest/java.web.kamehouse.git 
  cd /root/git/java.web.kamehouse 
  git checkout dev
}

setupDirectories() {
  logStep "Setup directories"

  # /root/home-synced
  mkdir -p /root/home-synced
  echo "docker" > /root/home-synced/host
  mkdir -p /root/home-synced/.kamehouse/keys/
  cp /root/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 /root/home-synced/.kamehouse/keys/kamehouse.pkcs12 
  cp /root/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt /root/home-synced/.kamehouse/keys/kamehouse.crt 
  cp /root/docker/keys/integration-test-cred.enc /root/home-synced/.kamehouse/

  # /root/.kamehouse/
  mkdir -p /root/.kamehouse
  cp /root/docker/keys/integration-test-cred.enc /root/.kamehouse/.vnc.server.pwd.enc
  cp /root/docker/keys/integration-test-cred.enc /root/.kamehouse/.unlock.screen.pwd.enc 

  # /root/logs
  mkdir -p /root/logs

  # /root/my.scripts
  cp -r /root/git/java.web.kamehouse/kamehouse-shell/my.scripts /root/
  chmod a+x -R /root/my.scripts
  # /root/my.scripts/.cred/.cred
  mkdir -p /root/my.scripts/.cred/
  cp /root/docker/keys/.cred /root/my.scripts/.cred/.cred

  # /root/programs
  mkdir -p /root/programs/
  mkdir -p /root/programs/apache-httpd
  mkdir -p /root/programs/kamehouse-cmd/bin
  mkdir -p /root/programs/kamehouse-cmd/lib

  ln -s /var/log/apache2 /root/programs/apache-httpd/logs
  ln -s /root /home/nbrest

  # Kamehouse ui static content:
  mkdir -p /var/www/html/kame-house
  cp -r /root/git/java.web.kamehouse/kamehouse-ui/src/main/webapp/* /var/www/html/kame-house
  rm -r /var/www/html/kame-house/WEB-INF

  # Kamehouse groot static content:
  mkdir -p /var/www/html/kame-house-groot
  cp -r /root/git/java.web.kamehouse/kamehouse-groot/public/kame-house-groot/* /var/www/html/kame-house-groot
  cp /root/git/java.web.kamehouse/kamehouse-groot/public/index.html /var/www/html/

  # Kamehouse faked dirs:
  mkdir -p /root/git/texts/video_playlists/http-niko-server/media-drive
}

setupEnv() {
  logStep "Setup env"
  source /root/.bashrc
}

setupMockedBins() {
  logStep "Setup mocked bins"
  chmod a+x /root/docker/bin/*
  cp /root/docker/mocked-bin/vncdo /usr/local/bin/vncdo
  cp /root/docker/mocked-bin/gnome-screensaver-command /usr/bin/gnome-screensaver-command
}

restartSshService() {
  logStep "Restart ssh service"
  service ssh restart
}

startMysql() {
  logStep "Start mysql"
  service mysql start
}

initKameHouseDb() {
  logStep "Init KameHouse database"
  echo "Importing setup-kamehouse.sql"
  mysql < /root/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/setup-kamehouse.sql
  echo "Importing spring-session.sql"
  mysql kameHouse < /root/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/spring-session.sql
  echo "Importing dump-kamehouse.sql"
  mysql kameHouse < /root/git/java.web.kamehouse/docker/mysql/dump-kamehouse.sql
}

startHttpd() {
  logStep "Start apache httpd"
  service apache2 start
}

startTomcat() {
  logStep "Start tomcat"
  cd /root/programs/apache-tomcat 
  bin/startup.sh
}

deployKamehouse() {
  logStep "Deploy KameHouse"
  /root/my.scripts/kamehouse/deploy-java-web-kamehouse.sh -f -p docker
}

keepContainerAlive() {
  echo -e "${COL_RED}*********************************************************${COL_NORMAL}"
  echo -e "${COL_RED} KameHouse docker init script finished.${COL_NORMAL}"
  echo -e "${COL_RED} Keep this terminal open while the container is running.${COL_NORMAL}"
  echo -e "${COL_RED}*********************************************************${COL_NORMAL}"

  echo "" > /root/.startup.lock
  tail -f /root/.startup.lock
  read
}

logStep() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_GREEN}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"