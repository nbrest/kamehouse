#!/bin/bash
# Init script to execute every time a docker instance starts

main() {
  echo "Docker startup script"
  cloneKameHouse
  setupBashDirectories
  setupEnvironment
  startMysql
  initKameHouseDb
  startHttpd
  startTomcat
  deployKamehouse
  keepAlive
}

cloneKameHouse() {
  echo "Cloning kamehouse"
  mkdir -p /root/git
  cd /root/git
  rm -rf /root/git/java.web.kamehouse 
  git clone https://github.com/nbrest/java.web.kamehouse.git 
  cd /root/git/java.web.kamehouse 
  git checkout dev
}

setupBashDirectories() {
  echo "Setup bash directories"

  # .cred
  mkdir -p /root/my.scripts/.cred/
  touch /root/my.scripts/.cred/.cred

  # /root/logs
  mkdir -p /root/logs

  # /root/my.scripts
  cp -r /root/git/java.web.kamehouse/kamehouse-shell/my.scripts /root/
  chmod a+x -R /root/my.scripts

  # /root/programs
  mkdir -p /root/programs/
  mkdir -p /root/programs/apache-httpd
  mkdir -p /root/programs/kamehouse-cmd/bin
  mkdir -p /root/programs/kamehouse-cmd/lib

  ln -s /usr/share/tomcat9 /root/programs/apache-tomcat
  ln -s /var/log/apache2 /root/programs/apache-httpd/logs
  ln -s /root /home/nbrest

  # bashrc:
  echo "" >> /root/.bashrc
  echo "source /root/my.scripts/lin/bashrc/bashrc.sh" >> /root/.bashrc
  echo "alias sudo=\"\"" >> /root/.bashrc

  # Kamehouse ui static content:
  mkdir -p /var/www/html/kame-house
  cp -r /root/git/java.web.kamehouse/kamehouse-ui/src/main/webapp/* /var/www/html/kame-house
  rm -r /var/www/html/kame-house/WEB-INF

  # Kamehouse groot static content:
  mkdir -p /var/www/html/kame-house-groot
  cp -r /root/git/java.web.kamehouse/kamehouse-groot/public/kame-house-groot/* /var/www/html/kame-house-groot

  # Kamehouse faked dirs:
  mkdir -p /root/git/texts/video_playlists/http-niko-server/media-drive
}

setupEnvironment() {
  echo "Setting path"
  PATH=${PATH}:${HOME}/my.scripts
}

startMysql() {
  echo "Starting mysql"
  service mysql start
}

initKameHouseDb() {
  echo "Importing setup-kamehouse.sql"
  mysql < /root/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/setup-kamehouse.sql
  echo "Importing spring-session.sql"
  mysql kameHouse < /root/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/spring-session.sql
  echo "Importing dump-kamehouse.sql"
  mysql kameHouse < /root/git/java.web.kamehouse/docker/mysql/dump-kamehouse.sql
}

startHttpd() {
  echo "Starting apache2"
  service apache2 start
}

startTomcat() {
  echo "Starting tomcat9"
  cd /root/programs/apache-tomcat 
  mkdir -p /usr/share/tomcat9/logs
  cp -r /usr/share/tomcat9/etc /usr/share/tomcat9/conf
  /usr/share/tomcat9/bin/startup.sh
}

deployKamehouse() {
  echo "Deploying kamehouse"
  source /root/.bashrc
  /root/my.scripts/kamehouse/deploy-java-web-kamehouse.sh -f
}

keepAlive() {
  echo "" > /root/.startup.lock
  tail -f /root/.startup.lock
  read
}

main "$@"