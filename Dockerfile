# BUILD: docker/scripts/docker-build-java-web-kamehouse.sh : build's the docker image and container from this file
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
RUN a2enmod headers
RUN a2enmod proxy
RUN a2enmod proxy_http
RUN a2enmod ssl
RUN a2enmod rewrite
RUN a2enmod proxy_wstunnel

# Setup users
COPY docker/etc/sudoers /etc/sudoers
RUN adduser --gecos "" --disabled-password nbrest
RUN echo 'nbrest:nbrest' | chpasswd

# Setup .bashrc
RUN echo "" >> /root/.bashrc
RUN echo "source /home/nbrest/my.scripts/lin/bashrc/bashrc.sh" >> /root/.bashrc
RUN echo "" >> /home/nbrest/.bashrc
RUN echo "source /home/nbrest/my.scripts/lin/bashrc/bashrc.sh" >> /home/nbrest/.bashrc

# Install tomcat
RUN mkdir -p /home/nbrest/programs
RUN cd /home/nbrest/programs ; wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.53/bin/apache-tomcat-9.0.53.tar.gz
RUN tar -xf /home/nbrest/programs/apache-tomcat-9.0.53.tar.gz -C /home/nbrest/programs/
RUN mv /home/nbrest/programs/apache-tomcat-9.0.53 /home/nbrest/programs/apache-tomcat
COPY docker/tomcat/server.xml /home/nbrest/programs/apache-tomcat/conf/
COPY docker/tomcat/tomcat-users.xml /home/nbrest/programs/apache-tomcat/conf/

# Clone KameHouse
RUN mkdir -p /home/nbrest/git
RUN chmod a+xwr /home/nbrest/git
RUN rm -rf /home/nbrest/git/java.web.kamehouse
RUN cd /home/nbrest/git ; git clone https://github.com/nbrest/java.web.kamehouse.git
RUN cd /home/nbrest/git/java.web.kamehouse ; git checkout dev

# Start mysql for initial dump
RUN service mysql start

# Copy docker setup folder
COPY docker /home/nbrest/docker

################## Setup directories ################################
# /home/nbrest/.config/vlc
RUN mkdir -p /home/nbrest/.config/vlc/
RUN cp /home/nbrest/docker/vlc/* /home/nbrest/.config/vlc/
# /home/nbrest/home-synced
RUN mkdir -p /home/nbrest/home-synced/.kamehouse/keys/
RUN cp /home/nbrest/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 /home/nbrest/home-synced/.kamehouse/keys/kamehouse.pkcs12
RUN cp /home/nbrest/git/java.web.kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt /home/nbrest/home-synced/.kamehouse/keys/kamehouse.crt
RUN cp /home/nbrest/docker/keys/integration-test-cred.enc /home/nbrest/home-synced/.kamehouse/
# /home/nbrest/.kamehouse/
RUN mkdir -p /home/nbrest/.kamehouse
RUN cp /home/nbrest/docker/keys/integration-test-cred.enc /home/nbrest/.kamehouse/.vnc.server.pwd.enc
RUN cp /home/nbrest/docker/keys/integration-test-cred.enc /home/nbrest/.kamehouse/.unlock.screen.pwd.enc
# /home/nbrest/logs
RUN mkdir -p /home/nbrest/logs
RUN mkdir -p /root/logs
# /home/nbrest/my.scripts
RUN cp -r /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts /home/nbrest/
RUN chmod a+x -R /home/nbrest/my.scripts
RUN ln -s /home/nbrest/my.scripts /root/my.scripts
# /home/nbrest/my.scripts/.cred/.cred
RUN mkdir -p /home/nbrest/my.scripts/.cred/
RUN cp /home/nbrest/docker/keys/.cred /home/nbrest/my.scripts/.cred/.cred
# /home/nbrest/programs
RUN mkdir -p /home/nbrest/programs/
RUN mkdir -p /home/nbrest/programs/apache-httpd
RUN mkdir -p /home/nbrest/programs/kamehouse-cmd/bin
RUN mkdir -p /home/nbrest/programs/kamehouse-cmd/lib
RUN ln -s /var/log/apache2 /home/nbrest/programs/apache-httpd/logs
# Kamehouse ui static content:
RUN ln -s /home/nbrest/git/java.web.kamehouse/kamehouse-ui/src/main/webapp /var/www/html/kame-house
# Kamehouse groot static content:
RUN ln -s /home/nbrest/git/java.web.kamehouse/kamehouse-groot/public/kame-house-groot /var/www/html/kame-house-groot
RUN rm /var/www/html/index.html
RUN ln -s /home/nbrest/git/java.web.kamehouse/kamehouse-groot/public/index.html /var/www/html/index.html
# Kamehouse faked dirs:
RUN mkdir -p /home/nbrest/git/texts/video_playlists/http-niko-server/media-drive/anime
RUN cp /home/nbrest/docker/media/playlist/dbz.m3u /home/nbrest/git/texts/video_playlists/http-niko-server/media-drive/anime/dbz.m3u
#####################################################################

# Fix permissions
RUN chown -R nbrest:users /home/nbrest

# Setup mocked bins
RUN chmod a+x /home/nbrest/docker/mocked-bin/*
RUN cp /home/nbrest/docker/mocked-bin/vncdo /usr/local/bin/vncdo
RUN cp /home/nbrest/docker/mocked-bin/gnome-screensaver-command /usr/bin/gnome-screensaver-command

# Intial dump of mysql data
RUN mysql < /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/setup-kamehouse.sql
RUN mysql kameHouse < /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts/kamehouse/sql/mysql/spring-session.sql
RUN mysql kameHouse < /home/nbrest/git/java.web.kamehouse/docker/mysql/dump-kamehouse.sql

# Open ports
EXPOSE 22 80 443 3306 8080 9090

# Initial deployment of kamehouse (this should download most of the dependencies at build and speed up startup)
RUN sudo su - nbrest -c "/home/nbrest/my.scripts/kamehouse/deploy-java-web-kamehouse.sh -f -p docker"

CMD ["/home/nbrest/docker/scripts/docker-init-java-web-kamehouse.sh"]