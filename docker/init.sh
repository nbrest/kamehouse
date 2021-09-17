#!/bin/bash
# Init script to execute every time a docker instance starts

main() {
  echo "Docker startup script"
  setupEnvironment
  startMysql
  initKameHouseDb
  startHttpd
  startTomcat
  keepAlive
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

keepAlive() {
  echo "" > /root/.startup.lock
  tail -f /root/.startup.lock
  read
}

main "$@"