# BUILD: docker/build.sh
# RUN: docker/run.sh

FROM ubuntu:20.04
LABEL maintainer="brest.nico@gmail.com"

# Disable interactions when building the image
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get upgrade -y

# Install git
RUN apt-get install -y git

# Install java
RUN apt-get install -y openjdk-11-jdk

# Install maven
RUN apt-get install -y maven

# Install tomcat9
RUN apt-get install -y tomcat9 tomcat9-admin tomcat9-docs tomcat9-user tomcat9-common tomcat9-examples

# Install apache2
RUN apt-get install -y apache2
RUN apt-get install -y php libapache2-mod-php

# Install mysql
RUN apt-get install -y mariadb-server

# Install vlc
#RUN apt-get install -y vlc

# Install zip
RUN apt-get install -y zip

# Clone kamehouse:
RUN mkdir -p /root/git
RUN cd /root/git ; git clone https://github.com/nbrest/java.web.kamehouse.git 
RUN cd /root/git/java.web.kamehouse ; git checkout dev

# Setup bash folders
RUN cp -r /root/git/java.web.kamehouse/kamehouse-shell/my.scripts /root/
RUN chmod a+x -R /root/my.scripts

RUN mkdir -p /root/programs/
RUN mkdir -p /root/programs/apache-httpd
RUN mkdir -p /root/programs/kamehouse-cmd/bin
RUN mkdir -p /root/programs/kamehouse-cmd/lib

RUN ln -s /usr/share/tomcat9 /home/nbrest/programs/apache-tomcat
RUN ln -s /var/log/apache2 /home/nbrest/programs/apache-httpd/logs

RUN ln -s /root /home/nbrest
RUN echo "" >> /root/.bashrc
RUN echo "source /root/my.scripts/lin/bashrc/bashrc.sh" >> /root/.bashrc
RUN echo "alias sudo=\"\"" >> /root/.bashrc

# Open ports
EXPOSE 22 80 443 3306 8080 9090

# Copy files
COPY docker /root/docker

CMD ["/root/docker/init.sh"]