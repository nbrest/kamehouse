#!/bin/bash
# Init script to execute every time a docker instance starts
source /root/.bashrc

main() {
  echo "Docker startup script"
  cloneKameHouse
  setupDirectories
  restartSshService
  startMysql
  initKameHouseDb
  startHttpd
  startTomcat
  deployKamehouse
  keepContainerAlive
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

setupDirectories() {
  echo "Setup directories"

  # /root/home-synced
  mkdir -p /root/home-synced
  echo "docker" > /root/home-synced/host
  mkdir -p /root/home-synced/.kamehouse/keys/
  cp /root/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 /root/home-synced/.kamehouse/keys/kamehouse.pkcs12 
  cp /root/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt /root/home-synced/.kamehouse/keys/kamehouse.crt 
  cp /root/docker/keys/integration-test-cred.enc /root/home-synced/.kamehouse/

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

restartSshService() {
  echo "Restarting ssh service"
  service ssh restart
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
  echo "Starting apache httpd"
  service apache2 start
}

startTomcat() {
  echo "Starting tomcat"
  cd /root/programs/apache-tomcat 
  bin/startup.sh
}

deployKamehouse() {
  echo "Deploying kamehouse"
  /root/my.scripts/kamehouse/deploy-java-web-kamehouse.sh -f -p docker
}

keepContainerAlive() {
  echo "" > /root/.startup.lock
  tail -f /root/.startup.lock
  read
}

main "$@"