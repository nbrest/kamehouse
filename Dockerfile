# BUILD: docker/build.sh
# START: docker/start.sh

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
RUN apt-get install -y tomcat9 tomcat9-admin tomcat9-docs tomcat9-user tomcat9-common

# Install apache2
RUN apt-get install -y apache2
RUN apt-get install -y php libapache2-mod-php

# Install mysql
RUN apt-get install -y mariadb-server

# Install vlc
#RUN apt-get install -y vlc

# Install zip
RUN apt-get install -y zip

# Open ports
EXPOSE 22 80 443 3306 8080 9090

# Copy files
COPY docker /root/docker

CMD ["/root/docker/init.sh"]