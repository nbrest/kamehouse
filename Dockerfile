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

# Setup users and apache httpd
COPY docker/apache2/conf /etc/apache2/conf
COPY docker/apache2/sites-available /etc/apache2/sites-available
COPY docker/apache2/certs/apache-selfsigned.crt /etc/ssl/certs/
COPY docker/apache2/certs/apache-selfsigned.key /etc/ssl/private/
COPY docker/apache2/robots.txt /var/www/html/

# When updating versions here, also update in /docs/versions/versions.md
ENV MAVEN_TOP_LEVEL_VERSION=3
ENV MAVEN_VERSION=3.9.3
ENV TOMCAT_TOP_LEVEL_VERSION=10
ENV TOMCAT_VERSION=10.1.11

# Setup users 
RUN adduser --gecos "" --disabled-password ${KAMEHOUSE_USERNAME} ; \
  echo "${KAMEHOUSE_USERNAME}:${KAMEHOUSE_PASSWORD}" | chpasswd ; \
  usermod -a -G adm ${KAMEHOUSE_USERNAME} ; \
  usermod -a -G sudo ${KAMEHOUSE_USERNAME} ; \
  # Setup apache httpd
  chown ${KAMEHOUSE_USERNAME}:users -R /var/www/html ; \
  ln -s /var/www/html/ /var/www/kamehouse-webserver ; \
  chown ${KAMEHOUSE_USERNAME}:users -R /var/www/kamehouse-webserver ; \
  a2ensite default-ssl ; \
  a2enmod headers proxy proxy_http proxy_wstunnel ssl rewrite ; \
  # Setup ${KAMEHOUSE_USERNAME} home
  sudo su - ${KAMEHOUSE_USERNAME} -c "echo \"source /home/${KAMEHOUSE_USERNAME}/.kamehouse/config/.kamehouse-docker-container-env\" >> /home/${KAMEHOUSE_USERNAME}/.bashrc ; \
    mkdir -p /home/${KAMEHOUSE_USERNAME}/.ssh" ; \
  # Install tomcat
  sudo su - ${KAMEHOUSE_USERNAME} -c "mkdir -p /home/${KAMEHOUSE_USERNAME}/programs ; \
  cd /home/${KAMEHOUSE_USERNAME}/programs ; \
  wget --no-check-certificate https://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_TOP_LEVEL_VERSION}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz ; \
  tar -xf /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /home/${KAMEHOUSE_USERNAME}/programs/ ; \
  mv /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat-${TOMCAT_VERSION} /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat ; \
  rm /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat-${TOMCAT_VERSION}.tar.gz ; \
  sed -i \"s#localhost:8000#0.0.0.0:8000#g\" /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat/bin/catalina.sh ; \
  # Install maven
  cd /home/${KAMEHOUSE_USERNAME}/programs ; \
  wget --no-check-certificate https://archive.apache.org/dist/maven/maven-${MAVEN_TOP_LEVEL_VERSION}/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz ; \
  tar -xf /home/${KAMEHOUSE_USERNAME}/programs/apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /home/${KAMEHOUSE_USERNAME}/programs/ ; \
  mv /home/${KAMEHOUSE_USERNAME}/programs/apache-maven-${MAVEN_VERSION} /home/${KAMEHOUSE_USERNAME}/programs/apache-maven ; \
  rm /home/${KAMEHOUSE_USERNAME}/programs/apache-maven-${MAVEN_VERSION}-bin.tar.gz ; \
  echo PATH=/home/${KAMEHOUSE_USERNAME}/programs/apache-maven/bin:\${PATH} >> /home/${KAMEHOUSE_USERNAME}/.bashrc ; \
  echo . /home/${KAMEHOUSE_USERNAME}/.env >> /home/${KAMEHOUSE_USERNAME}/.bashrc" ; \
  echo "PATH=/home/${KAMEHOUSE_USERNAME}/programs/apache-maven/bin:${PATH}" >> /etc/profile ; \
  ### Setup directories ###
  # /home/${KAMEHOUSE_USERNAME}/.config/vlc
  sudo su - ${KAMEHOUSE_USERNAME} -c "mkdir -p /home/${KAMEHOUSE_USERNAME}/.config/vlc/" ; \
  # /home/${KAMEHOUSE_USERNAME}/programs
  sudo su - ${KAMEHOUSE_USERNAME} -c "mkdir -p /home/${KAMEHOUSE_USERNAME}/programs/apache-httpd ; \
  mkdir -p /home/${KAMEHOUSE_USERNAME}/programs/kamehouse-cmd/bin ; \
  mkdir -p /home/${KAMEHOUSE_USERNAME}/programs/kamehouse-cmd/lib" ; \
  chmod a+rx /var/log/apache2 ; \
  ln -s /var/log/apache2 /home/${KAMEHOUSE_USERNAME}/programs/apache-httpd/logs ; \
  # Setup mocked bins
  mv /usr/bin/vlc /usr/bin/vlc-bin ; \
  # Setup bash prompt colors
  sed -i "s/#force_color_prompt=yes/force_color_prompt=yes/I" /root/.bashrc ; \
  sed -i "s/01;32m/01;31m/I" /root/.bashrc

# Setup mocked bins
COPY docker/mocked-bin/vlc /usr/bin/vlc
COPY docker/mocked-bin/gnome-screensaver-command /usr/bin/gnome-screensaver-command
# Setup tomcat
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/tomcat/server.xml /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat/conf/
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/tomcat/tomcat-users.xml /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat/conf/
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/tomcat/manager.xml /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat/conf/Catalina/localhost/
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/tomcat/host-manager.xml /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat/conf/Catalina/localhost/
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/maven/settings.xml /home/${KAMEHOUSE_USERNAME}/programs/apache-maven/conf/settings.xml
### Setup directories ###
# /home/${KAMEHOUSE_USERNAME}/.config/vlc
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/vlc/* /home/${KAMEHOUSE_USERNAME}/.config/vlc/

# Copy docker setup folder
COPY --chown=${KAMEHOUSE_USERNAME}:users docker /home/${KAMEHOUSE_USERNAME}/docker

# Setup mocked bins
RUN chmod a+rx /usr/bin/vlc ; \
  chmod a+rx /usr/bin/gnome-screensaver-command

# Run docker-build-kamehouse.sh with -b to skip docker cache from this point onwards
ARG BUILD_DATE_KAMEHOUSE=0000-00-00
RUN echo "${BUILD_DATE_KAMEHOUSE}" > /home/${KAMEHOUSE_USERNAME}/.docker-image-build-date; 

ARG DOCKER_IMAGE_TAG
RUN sudo su - ${KAMEHOUSE_USERNAME} -c "echo DOCKER_IMAGE_TAG=${DOCKER_IMAGE_TAG} >> /home/${KAMEHOUSE_USERNAME}/.env ; \
  mkdir -p /home/${KAMEHOUSE_USERNAME}/git ; \
  chmod a+xwr /home/${KAMEHOUSE_USERNAME}/git ; \
  rm -rf /home/${KAMEHOUSE_USERNAME}/git/kamehouse ; \
  cd /home/${KAMEHOUSE_USERNAME}/git ; \
  # Clone and deploy kamehouse
  git clone https://github.com/nbrest/kamehouse.git ; \
  cd /home/${KAMEHOUSE_USERNAME}/git/kamehouse ; \
  # Checkout git branch
  chmod a+x /home/${KAMEHOUSE_USERNAME}/docker/scripts/* ; \
  /home/${KAMEHOUSE_USERNAME}/docker/scripts/dockerfile-git-checkout.sh ${DOCKER_IMAGE_TAG} ; \
  chmod a+x ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh ; \
  ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh ; \
  /home/${KAMEHOUSE_USERNAME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-container/docker-init-kamehouse-folder-to-defaults.sh ; \
  /home/${KAMEHOUSE_USERNAME}/programs/kamehouse-shell/bin/kamehouse/deploy-kamehouse.sh -c -p docker ; \
  # Clear temporary files
  /home/${KAMEHOUSE_USERNAME}/programs/apache-maven/bin/mvn clean ; \
  rm -rf /home/${KAMEHOUSE_USERNAME}/.m2/repository/com/nicobrest ; \
  # And recreate sample video playlists directories
  /home/${KAMEHOUSE_USERNAME}/programs/kamehouse-shell/bin/kamehouse/create-sample-video-playlists.sh" ; \
  # Configure sudoers for kamehouse
  /home/${KAMEHOUSE_USERNAME}/programs/kamehouse-shell/bin/kamehouse/set-kamehouse-sudoers-permissions.sh -u ${KAMEHOUSE_USERNAME} ; \
  # Install groot
  /home/${KAMEHOUSE_USERNAME}/programs/kamehouse-shell/bin/kamehouse/install-kamehouse-groot.sh -u ${KAMEHOUSE_USERNAME} ; \
  # Httpd root index.html
  rm /var/www/html/index.html ; \
  cp /home/${KAMEHOUSE_USERNAME}/git/kamehouse/kamehouse-groot/src/main/public/index.html /var/www/html/index.html ; \
  # Open mariadb to external connections and intial dump of mariadb data
  sed -i "s#bind-address            = 127.0.0.1#bind-address            = 0.0.0.0#g" /etc/mysql/mariadb.conf.d/50-server.cnf ; \
  service mariadb start ; \
  sleep 5 ; \
  service mariadb start ; \
  sleep 5 ; \
  service mariadb start ; \
  sleep 5 ; \
  service mariadb start ; \
  sleep 5 ; \
  service mariadb start ; \
  mariadb -e"set @kameHousePass = '`cat /home/${KAMEHOUSE_USERNAME}/docker/keys/.kamehouse-secrets.cfg | grep MARIADB_PASS_KAMEHOUSE | cut -d '=' -f 2`'; `cat /home/${KAMEHOUSE_USERNAME}/git/kamehouse/kamehouse-shell/sql/mariadb/add-kamehouse-user.sql`" ; \
  mariadb < /home/${KAMEHOUSE_USERNAME}/git/kamehouse/kamehouse-shell/sql/mariadb/create-kamehouse-schema.sql ; \
  mariadb kamehouse < /home/${KAMEHOUSE_USERNAME}/git/kamehouse/kamehouse-shell/sql/mariadb/spring-session.sql ; \
  mariadb kamehouse < /home/${KAMEHOUSE_USERNAME}/git/kamehouse/kamehouse-shell/sql/mariadb/dump-kamehouse.sql

# Expose ports
EXPOSE 22 80 443 3306 5000 8000 8080 9090

# Set timezone
ENV TZ=Australia/Sydney
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

CMD "/home/${KAMEHOUSE_USERNAME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-container/docker-init-kamehouse.sh"