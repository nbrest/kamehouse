# BUILD: docker-build-kamehouse.sh : builds the docker image from this file
# RUN: docker-run-kamehouse.sh : runs a temporary container from an image built from this file

ARG DOCKER_IMAGE_BASE
FROM ${DOCKER_IMAGE_BASE}
LABEL maintainer="brest.nico@gmail.com"

# Disable interactions when building the image
ENV DEBIAN_FRONTEND=noninteractive

# Install dependencies
RUN apt-get update -y && apt-get -y upgrade ; \
  apt-get install -y apache2 ; \
  apt-get install -y curl ; \
  apt-get install -y git ; \
  apt-get install -y iputils-ping ; \
  apt-get install -y openjdk-17-jdk ; \
  apt-get install -y mariadb-server ; \
  apt-get install -y net-tools ; \
  apt-get install -y openssh-server ; \
  apt-get install -y php libapache2-mod-php php-mysql ; \
  apt-get install -y python3.11 ; \
  apt-get install -y python3-pyqt5 ; \
  apt-get install -y screen ; \
  apt-get install -y sudo ; \
  apt-get install -y tightvncserver ; \
  apt-get install -y vim ; \
  apt-get install -y vlc ; \
  apt-get install -y zip ; \
  apt-get autopurge -y ; \
  apt-get autoclean -y ; \
  apt-get clean -y

# Install node
RUN cd ~ ; \
  curl -sL https://deb.nodesource.com/setup_20.x | sudo bash - ; \
  sudo apt-get install nodejs -y ; \
  apt-get autopurge -y ; \
  apt-get autoclean -y ; \
  apt-get clean -y ; \
  npm install -g typescript

# Setup users 
ARG KAMEHOUSE_USERNAME=goku
ENV KAMEHOUSE_USERNAME=${KAMEHOUSE_USERNAME}
ARG KAMEHOUSE_PASSWORD=gohan
ENV KAMEHOUSE_PASSWORD=${KAMEHOUSE_PASSWORD}

# Copy docker setup folder
COPY --chown=${KAMEHOUSE_USERNAME}:users docker /home/${KAMEHOUSE_USERNAME}/docker
RUN chmod a+x /home/${KAMEHOUSE_USERNAME}/docker/scripts/*

# Setup container user, apps, folders
RUN /home/${KAMEHOUSE_USERNAME}/docker/scripts/dockerfile-root-setup-container.sh -u ${KAMEHOUSE_USERNAME} -p ${KAMEHOUSE_PASSWORD}

# Run docker-build-kamehouse.sh with -b to skip docker cache from this point onwards
ARG BUILD_DATE_KAMEHOUSE=0000-00-00
RUN echo "${BUILD_DATE_KAMEHOUSE}" > /home/${KAMEHOUSE_USERNAME}/.docker-image-build-date; 

ARG DOCKER_IMAGE_TAG
RUN sudo su - ${KAMEHOUSE_USERNAME} -c "/home/${KAMEHOUSE_USERNAME}/docker/scripts/dockerfile-user-setup-kamehouse.sh -u ${KAMEHOUSE_USERNAME} -t ${DOCKER_IMAGE_TAG} " ; \
  /home/${KAMEHOUSE_USERNAME}/docker/scripts/dockerfile-root-setup-kamehouse.sh -u ${KAMEHOUSE_USERNAME}

# Expose ports
EXPOSE 22 80 443 3306 5000 8000 8080 9090

# Set timezone
ENV TZ=Australia/Sydney
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

CMD "/home/${KAMEHOUSE_USERNAME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-container/docker-init-kamehouse.sh"