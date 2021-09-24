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
  sudo su - ${USERNAME} -c "mkdir -p /home/nbrest/git"
  sudo su - ${USERNAME} -c "chmod a+xwr /home/nbrest/git"
  sudo su - ${USERNAME} -c "rm -rf /home/nbrest/git/java.web.kamehouse"
  sudo su - ${USERNAME} -c "cd /home/nbrest/git ; git clone https://github.com/nbrest/java.web.kamehouse.git"
  sudo su - ${USERNAME} -c "cd /home/nbrest/git/java.web.kamehouse ; git checkout dev"
}

setupDirectories() {
  logStep "Setup directories"

  # /home/nbrest/.config/vlc
  sudo su - ${USERNAME} -c "mkdir -p /home/nbrest/.config/vlc/"
  sudo su - ${USERNAME} -c "cp /home/nbrest/docker/vlc/* /home/nbrest/.config/vlc/"
  
  # /home/nbrest/home-synced
  sudo su - ${USERNAME} -c "mkdir -p /home/nbrest/home-synced/.kamehouse/keys/"
  sudo su - ${USERNAME} -c "cp /home/nbrest/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 /home/nbrest/home-synced/.kamehouse/keys/kamehouse.pkcs12"
  sudo su - ${USERNAME} -c "cp /home/nbrest/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt /home/nbrest/home-synced/.kamehouse/keys/kamehouse.crt"
  sudo su - ${USERNAME} -c "cp /home/nbrest/docker/keys/integration-test-cred.enc /home/nbrest/home-synced/.kamehouse/"

  # /home/nbrest/.kamehouse/
  sudo su - ${USERNAME} -c "mkdir -p /home/nbrest/.kamehouse"
  sudo su - ${USERNAME} -c "cp /home/nbrest/docker/keys/integration-test-cred.enc /home/nbrest/.kamehouse/.vnc.server.pwd.enc"
  sudo su - ${USERNAME} -c "cp /home/nbrest/docker/keys/integration-test-cred.enc /home/nbrest/.kamehouse/.unlock.screen.pwd.enc"

  # /home/nbrest/logs
  sudo su - ${USERNAME} -c "mkdir -p /home/nbrest/logs"
  mkdir -p /root/logs

  # /home/nbrest/my.scripts
  sudo su - ${USERNAME} -c "cp -r /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts /home/nbrest/"
  sudo su - ${USERNAME} -c "chmod a+x -R /home/nbrest/my.scripts"
  ln -s /home/nbrest/my.scripts /root/my.scripts

  # /home/nbrest/my.scripts/.cred/.cred
  sudo su - ${USERNAME} -c "mkdir -p /home/nbrest/my.scripts/.cred/"
  sudo su - ${USERNAME} -c "cp /home/nbrest/docker/keys/.cred /home/nbrest/my.scripts/.cred/.cred"

  # /home/nbrest/programs
  sudo su - ${USERNAME} -c "mkdir -p /home/nbrest/programs/"
  sudo su - ${USERNAME} -c "mkdir -p /home/nbrest/programs/apache-httpd"
  sudo su - ${USERNAME} -c "mkdir -p /home/nbrest/programs/kamehouse-cmd/bin"
  sudo su - ${USERNAME} -c "mkdir -p /home/nbrest/programs/kamehouse-cmd/lib"

  sudo su - ${USERNAME} -c "ln -s /var/log/apache2 /home/nbrest/programs/apache-httpd/logs"

  # Kamehouse ui static content:
  ln -s /home/nbrest/git/java.web.kamehouse/kamehouse-ui/src/main/webapp /var/www/html/kame-house

  # Kamehouse groot static content:
  ln -s /home/nbrest/git/java.web.kamehouse/kamehouse-groot/public/kame-house-groot /var/www/html/kame-house-groot
  rm /var/www/html/index.html
  ln -s /home/nbrest/git/java.web.kamehouse/kamehouse-groot/public/index.html /var/www/html/index.html

  # Kamehouse faked dirs:
  sudo su - ${USERNAME} -c "mkdir -p /home/nbrest/git/texts/video_playlists/http-niko-server/media-drive/anime"
  sudo su - ${USERNAME} -c "cp /home/nbrest/docker/media/playlist/dbz.m3u /home/nbrest/git/texts/video_playlists/http-niko-server/media-drive/anime/dbz.m3u"
}

setupEnv() {
  logStep "Setup env"
  source /root/.bashrc
}

setupMockedBins() {
  logStep "Setup mocked bins"
  sudo su - ${USERNAME} -c "chmod a+x /home/nbrest/docker/mocked-bin/*"
  cp /home/nbrest/docker/mocked-bin/vncdo /usr/local/bin/vncdo
  cp /home/nbrest/docker/mocked-bin/gnome-screensaver-command /usr/bin/gnome-screensaver-command
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
  mysql < /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/setup-kamehouse.sql
  echo "Importing spring-session.sql"
  mysql kameHouse < /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/spring-session.sql
  echo "Importing dump-kamehouse.sql"
  mysql kameHouse < /home/nbrest/git/java.web.kamehouse/docker/mysql/dump-kamehouse.sql
}

startHttpd() {
  logStep "Start apache httpd"
  rm /var/run/apache2/apache2.pid
  service apache2 start
}

startTomcat() {
  logStep "Start tomcat"
  sudo su - ${USERNAME} -c "cd /home/nbrest/programs/apache-tomcat ; \
  USER_UID=`sudo cat /etc/passwd | grep ${USERNAME} | cut -d ':' -f3` ; \
  DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/${USER_UID}/bus DISPLAY=:0.0 bin/startup.sh"
}

deployKamehouse() {
  logStep "Deploy KameHouse"
  sudo su - ${USERNAME} -c "/home/nbrest/my.scripts/kamehouse/deploy-java-web-kamehouse.sh -f -p docker"
  logStep "Finished building KameHouse"
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