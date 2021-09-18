# BUILD: docker/build.sh : build's the docker image and container from this file
# START: docker/start.sh : starts the container built from this script

FROM ubuntu:20.04
LABEL maintainer="brest.nico@gmail.com"

# Disable interactions when building the image
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get -y update && apt-get -y upgrade 

# Install dependencies
RUN apt-get install -y git
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y maven
RUN apt-get install -y apache2
RUN apt-get install -y php libapache2-mod-php
RUN apt-get install -y mariadb-server
RUN apt-get install -y vlc
RUN apt-get install -y zip
RUN apt-get install -y vim
RUN apt-get install -y openssh-server

# Install tomcat
RUN mkdir -p /root/programs
RUN cd /root/programs ; wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.53/bin/apache-tomcat-9.0.53.tar.gz
RUN tar -xf /root/programs/apache-tomcat-9.0.53.tar.gz -C /root/programs/
RUN mv /root/programs/apache-tomcat-9.0.53 /root/programs/apache-tomcat
COPY docker/tomcat/server.xml /root/programs/apache-tomcat/conf/
COPY docker/tomcat/tomcat-users.xml /root/programs/apache-tomcat/conf/

# Open root ssh login (for dev only!)
COPY docker/ssh/sshd_config /etc/ssh/sshd_config
RUN echo 'root:change-me' | chpasswd

# Setup .bashrc
RUN echo "" >> /root/.bashrc
RUN echo "source /root/my.scripts/lin/bashrc/bashrc.sh" >> /root/.bashrc

# Open ports
EXPOSE 22 80 443 3306 8080 9090

# Copy docker setup folder
COPY docker /root/docker

CMD ["/root/docker/init.sh"]