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
RUN chown -R nbrest:users /home/nbrest

# Copy docker setup folder
COPY docker /home/nbrest/docker
RUN chown -R nbrest:users /home/nbrest/docker

# Open ports
EXPOSE 22 80 443 3306 8080 9090

CMD ["/home/nbrest/docker/scripts/docker-init-java-web-kamehouse.sh"]