# BUILD: docker/scripts/docker-build-java-web-kamehouse.sh : build's the docker image from this file
# RUN: docker/scripts/docker-run-java-web-kamehouse.sh : runs a temporary container from an image built from this file

FROM ubuntu:20.04
LABEL maintainer="brest.nico@gmail.com"

# Disable interactions when building the image
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update -y && apt-get -y upgrade 

# Install dependencies
RUN apt-get install -y apache2
RUN apt-get install -y curl
RUN apt-get install -y git
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y mariadb-server
RUN apt-get install -y maven
RUN apt-get install -y net-tools
RUN apt-get install -y openssh-server
RUN apt-get install -y php libapache2-mod-php
RUN apt-get install -y screen
RUN apt-get install -y sudo
RUN apt-get install -y tightvncserver
RUN apt-get install -y vim
RUN apt-get install -y vlc
RUN apt-get install -y zip

# Setup apache httpd
COPY docker/apache2/conf /etc/apache2/conf
COPY docker/apache2/sites-available /etc/apache2/sites-available
COPY docker/apache2/certs/apache-selfsigned.crt /etc/ssl/certs/
COPY docker/apache2/certs/apache-selfsigned.key /etc/ssl/private/
COPY docker/apache2/.htpasswd /var/www/html/
RUN ln -s /var/www/html/ /var/www/kh.webserver
RUN a2ensite default-ssl
RUN a2enmod headers proxy proxy_http proxy_wstunnel ssl rewrite 

# Setup users
COPY docker/etc/sudoers /etc/sudoers
RUN adduser --gecos "" --disabled-password nbrest ; \
  echo 'nbrest:nbrest' | chpasswd

# Setup .bashrc
RUN echo "source /home/nbrest/my.scripts/lin/bashrc/bashrc.sh" >> /root/.bashrc
RUN sudo su - nbrest -c "echo \"source /home/nbrest/my.scripts/lin/bashrc/bashrc.sh\" >> /home/nbrest/.bashrc"

# Install tomcat
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/programs ; \
  cd /home/nbrest/programs ; \
  wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.53/bin/apache-tomcat-9.0.53.tar.gz ; \
  tar -xf /home/nbrest/programs/apache-tomcat-9.0.53.tar.gz -C /home/nbrest/programs/ ; \
  mv /home/nbrest/programs/apache-tomcat-9.0.53 /home/nbrest/programs/apache-tomcat ; \
  rm /home/nbrest/programs/apache-tomcat-9.0.53.tar.gz"
COPY --chown=nbrest:users docker/tomcat/server.xml /home/nbrest/programs/apache-tomcat/conf/
COPY --chown=nbrest:users docker/tomcat/tomcat-users.xml /home/nbrest/programs/apache-tomcat/conf/
COPY --chown=nbrest:users docker/tomcat/manager.xml /home/nbrest/programs/apache-tomcat/conf/Catalina/localhost/
COPY --chown=nbrest:users docker/tomcat/host-manager.xml /home/nbrest/programs/apache-tomcat/conf/Catalina/localhost/

# Clone KameHouse dev branch
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/git ; \
  chmod a+xwr /home/nbrest/git ; \
  rm -rf /home/nbrest/git/java.web.kamehouse ; \
  cd /home/nbrest/git ; \
  git clone https://github.com/nbrest/java.web.kamehouse.git ; \
  cd /home/nbrest/git/java.web.kamehouse ; \
  git checkout dev ; \
  git branch -D master"

# Build kamehouse to download all the maven dependencies (then clean the target directories)
RUN sudo su - nbrest -c "cd /home/nbrest/git/java.web.kamehouse ; \
  mvn clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true ; \
  mvn clean ; \
  rm -rf /home/nbrest/.m2/repository/com/nicobrest"

################## Setup directories ################################
# /home/nbrest/.config/vlc
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/.config/vlc/"
COPY --chown=nbrest:users docker/vlc/* /home/nbrest/.config/vlc/
# /home/nbrest/home-synced
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/home-synced/.kamehouse/keys ; \
  cp /home/nbrest/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 /home/nbrest/home-synced/.kamehouse/keys/kamehouse.pkcs12 ; \
  cp /home/nbrest/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt /home/nbrest/home-synced/.kamehouse/keys/kamehouse.crt"
COPY --chown=nbrest:users docker/keys/integration-test-cred.enc /home/nbrest/home-synced/.kamehouse/
# /home/nbrest/.kamehouse/
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/.kamehouse"
COPY --chown=nbrest:users docker/keys/integration-test-cred.enc /home/nbrest/.kamehouse/.vnc.server.pwd.enc
COPY --chown=nbrest:users docker/keys/integration-test-cred.enc /home/nbrest/.kamehouse/.unlock.screen.pwd.enc
# /home/nbrest/logs
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/logs"
RUN mkdir -p /root/logs
# /home/nbrest/my.scripts
RUN sudo su - nbrest -c "cp -r /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts /home/nbrest/ ; \
  chmod a+x -R /home/nbrest/my.scripts"
RUN ln -s /home/nbrest/my.scripts /root/my.scripts
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/my.scripts/.cred/"
COPY --chown=nbrest:users docker/keys/.cred /home/nbrest/my.scripts/.cred/.cred
# /home/nbrest/programs
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/programs/apache-httpd ; \
  mkdir -p /home/nbrest/programs/kamehouse-cmd/bin ; \
  mkdir -p /home/nbrest/programs/kamehouse-cmd/lib"
RUN ln -s /var/log/apache2 /home/nbrest/programs/apache-httpd/logs
# Kamehouse ui static content:
RUN ln -s /home/nbrest/git/java.web.kamehouse/kamehouse-ui/src/main/webapp /var/www/html/kame-house
# Kamehouse groot static content:
RUN ln -s /home/nbrest/git/java.web.kamehouse/kamehouse-groot/public/kame-house-groot /var/www/html/kame-house-groot
RUN rm /var/www/html/index.html ; \
  ln -s /home/nbrest/git/java.web.kamehouse/kamehouse-groot/public/index.html /var/www/html/index.html
# Kamehouse faked dirs:
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/git/texts/video_playlists/http-niko-server/media-drive/anime"
COPY --chown=nbrest:users docker/media/playlist/dbz.m3u /home/nbrest/git/texts/video_playlists/http-niko-server/media-drive/anime/dbz.m3u
#####################################################################

# Setup mocked bins
COPY docker/mocked-bin/vncdo /usr/local/bin/vncdo
COPY docker/mocked-bin/gnome-screensaver-command /usr/bin/gnome-screensaver-command
RUN chmod a+x /usr/local/bin/vncdo ; \
  chmod a+x /usr/bin/gnome-screensaver-command

# Intial dump of mysql data
RUN service mysql start ; \
  sleep 5 ; \
  mysql < /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/setup-kamehouse.sql ; \
  mysql kameHouse < /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/spring-session.sql ; \
  mysql kameHouse < /home/nbrest/git/java.web.kamehouse/docker/mysql/dump-kamehouse.sql

# Copy docker setup folder
COPY --chown=nbrest:users docker /home/nbrest/docker

# Deploy latest version of kamehouse (should have most of the dependencies already downloaded)
RUN sudo su - nbrest -c "cd /home/nbrest/git/java.web.kamehouse ; \
  git pull origin dev ; \
  /home/nbrest/my.scripts/kamehouse/deploy-java-web-kamehouse.sh -f -p docker ; \
  cd /home/nbrest/git/java.web.kamehouse ; \
  mvn clean ; \
  rm -rf /home/nbrest/.m2/repository/com/nicobrest ; \
  /home/nbrest/docker/scripts/docker-my-scripts-update.sh"

# Expose ports
EXPOSE 22 80 443 3306 8080 9090

# Setup default env for container init script
ENV PULL_KAMEHOUSE=true

CMD ["/home/nbrest/docker/scripts/docker-init-java-web-kamehouse.sh"]