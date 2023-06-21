FROM ubuntu:22.04
LABEL maintainer="brest.nico@gmail.com"

# Disable interactions when building the image
ENV DEBIAN_FRONTEND=noninteractive

# Install dependencies
RUN apt-get update -y && apt-get -y upgrade ; \
  apt-get install -y apache2 ; \
  apt-get install -y curl ; \
  apt-get install -y git ; \
  apt-get install -y iputils-ping ; \
  apt-get install -y openjdk-8-jdk ; \
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

# Setup users 
ARG KAMEHOUSE_USERNAME=goku
ENV KAMEHOUSE_USERNAME=${KAMEHOUSE_USERNAME}
ARG KAMEHOUSE_PASSWORD=gohan
ENV KAMEHOUSE_PASSWORD=${KAMEHOUSE_PASSWORD}

# Setup users and apache httpd
COPY docker/etc/sudoers /etc/sudoers
COPY docker/apache2/conf /etc/apache2/conf
COPY docker/apache2/sites-available /etc/apache2/sites-available
COPY docker/apache2/certs/apache-selfsigned.crt /etc/ssl/certs/
COPY docker/apache2/certs/apache-selfsigned.key /etc/ssl/private/
COPY docker/apache2/robots.txt /var/www/html/

ENV MAVEN_VERSION=3.3.9
ENV TOMCAT_VERSION=8.5.49

# Setup users 
RUN adduser --gecos "" --disabled-password ${KAMEHOUSE_USERNAME} ; \
  echo "${KAMEHOUSE_USERNAME}:${KAMEHOUSE_PASSWORD}" | chpasswd ; \
  usermod -a -G adm ${KAMEHOUSE_USERNAME} ; \
  usermod -a -G sudo ${KAMEHOUSE_USERNAME} ; \
  adduser --gecos "" --disabled-password nbrest ; \
  echo "nbrest:nbrest" | chpasswd ; \
  usermod -a -G adm nbrest ; \
  usermod -a -G sudo nbrest ; \
  # Setup apache httpd
  chown ${KAMEHOUSE_USERNAME}:users -R /var/www/html ; \
  ln -s /var/www/html/ /var/www/kamehouse-webserver ; \
  ln -s /var/www/html/ /var/www/kh.webserver ; \
  chown ${KAMEHOUSE_USERNAME}:users -R /var/www/kamehouse-webserver ; \
  chown ${KAMEHOUSE_USERNAME}:users -R /var/www/kh.webserver ; \
  a2ensite default-ssl ; \
  a2enmod headers proxy proxy_http proxy_wstunnel ssl rewrite ; \
  # Setup ${KAMEHOUSE_USERNAME} home
  sudo su - ${KAMEHOUSE_USERNAME} -c "mkdir -p /home/${KAMEHOUSE_USERNAME}/.ssh" ; \
  # Install tomcat
  sudo su - ${KAMEHOUSE_USERNAME} -c "mkdir -p /home/${KAMEHOUSE_USERNAME}/programs ; \
  cd /home/${KAMEHOUSE_USERNAME}/programs ; \
  wget --no-check-certificate https://archive.apache.org/dist/tomcat/tomcat-8/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz ; \
  tar -xf /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /home/${KAMEHOUSE_USERNAME}/programs/ ; \
  mv /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat-${TOMCAT_VERSION} /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat ; \
  rm /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat-${TOMCAT_VERSION}.tar.gz ; \
  sed -i \"s#localhost:8000#0.0.0.0:8000#g\" /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat/bin/catalina.sh ; \
  # Install maven
  cd /home/${KAMEHOUSE_USERNAME}/programs ; \
  wget --no-check-certificate https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz ; \
  tar -xf /home/${KAMEHOUSE_USERNAME}/programs/apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /home/${KAMEHOUSE_USERNAME}/programs/ ; \
  mv /home/${KAMEHOUSE_USERNAME}/programs/apache-maven-${MAVEN_VERSION} /home/${KAMEHOUSE_USERNAME}/programs/apache-maven ; \
  rm /home/${KAMEHOUSE_USERNAME}/programs/apache-maven-${MAVEN_VERSION}-bin.tar.gz ; \
  echo PATH=/home/${KAMEHOUSE_USERNAME}/programs/apache-maven/bin:\${PATH} >> /home/${KAMEHOUSE_USERNAME}/.bashrc ; \
  echo . /home/${KAMEHOUSE_USERNAME}/.env >> /home/${KAMEHOUSE_USERNAME}/.bashrc" ; \
  echo "PATH=/home/${KAMEHOUSE_USERNAME}/programs/apache-maven/bin:${PATH}" >> /etc/profile ; \
  ### Setup directories ###
  # /home/${KAMEHOUSE_USERNAME}/.config/vlc
  sudo su - ${KAMEHOUSE_USERNAME} -c "mkdir -p /home/${KAMEHOUSE_USERNAME}/.config/vlc/" ; \
  # /home/${KAMEHOUSE_USERNAME}/.kamehouse/
  sudo su - ${KAMEHOUSE_USERNAME} -c "mkdir -p /home/${KAMEHOUSE_USERNAME}/.kamehouse" ; \
  # /home/${KAMEHOUSE_USERNAME}/programs/kamehouse-shell/bin
  sudo su - ${KAMEHOUSE_USERNAME} -c "mkdir -p /home/${KAMEHOUSE_USERNAME}/.kamehouse/.shell/" ; \
  # /home/${KAMEHOUSE_USERNAME}/programs
  sudo su - ${KAMEHOUSE_USERNAME} -c "mkdir -p /home/${KAMEHOUSE_USERNAME}/programs/apache-httpd ; \
  mkdir -p /home/${KAMEHOUSE_USERNAME}/programs/kamehouse-cmd/bin ; \
  mkdir -p /home/${KAMEHOUSE_USERNAME}/programs/kamehouse-cmd/lib" ; \
  chmod a+rx /var/log/apache2 ; \
  ln -s /var/log/apache2 /home/${KAMEHOUSE_USERNAME}/programs/apache-httpd/logs ; \
   # Setup mocked bins
  mv /usr/bin/vlc /usr/bin/vlc-bin ; \
  # Setup for exec-script.sh through groot
  ln -s /home/${KAMEHOUSE_USERNAME}/my.scripts /home/nbrest/my.scripts ; \
  ln -s /home/${KAMEHOUSE_USERNAME}/my.scripts /root/my.scripts ; \
  ln -s /home/${KAMEHOUSE_USERNAME}/my.scripts /var/www/my.scripts ; \
  ln -s /home/${KAMEHOUSE_USERNAME}/programs /home/nbrest/programs ; \
  chmod -R a+rwx /home/nbrest ; \
  # setup logs dir
  sudo su - ${KAMEHOUSE_USERNAME} -c "mkdir -p /home/${KAMEHOUSE_USERNAME}/logs" ; \
  sudo su - nbrest -c "mkdir -p /home/nbrest/logs"
  
# Setup mocked bins
COPY docker/mocked-bin/vlc /usr/bin/vlc
COPY docker/mocked-bin/vncdo /usr/local/bin/vncdo
COPY docker/mocked-bin/gnome-screensaver-command /usr/bin/gnome-screensaver-command
### Setup directories ###
# /home/${KAMEHOUSE_USERNAME}/.config/vlc
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/vlc/* /home/${KAMEHOUSE_USERNAME}/.config/vlc/
# /home/${KAMEHOUSE_USERNAME}/.kamehouse/
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/keys/.vnc.server.pwd.enc /home/${KAMEHOUSE_USERNAME}/.kamehouse/
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/keys/.unlock.screen.pwd.enc /home/${KAMEHOUSE_USERNAME}/.kamehouse/
# /home/${KAMEHOUSE_USERNAME}/programs/kamehouse-shell/bin
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/keys/.cred /home/${KAMEHOUSE_USERNAME}/.kamehouse/.shell/.cred
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/keys/.cred /home/${KAMEHOUSE_USERNAME}/my.scripts/.cred/.cred
# Copy .htpasswd
COPY docker/apache2/.htpasswd /var/www/html/.htpasswd
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/keys/integration-test-cred.enc /home/${KAMEHOUSE_USERNAME}/home-synced/.kamehouse/

# Setup mocked bins
RUN chmod a+x /usr/bin/vlc ; \
  chmod a+x /usr/local/bin/vncdo ; \
  chmod a+x /usr/bin/gnome-screensaver-command ; \
  # setup nbrest home
  ln -s /home/${KAMEHOUSE_USERNAME}/home-synced /home/nbrest/home-synced ; \
  ln -s /home/${KAMEHOUSE_USERNAME}/.kamehouse /home/nbrest/.kamehouse ; \
  ln -s /home/${KAMEHOUSE_USERNAME}/.config /home/nbrest/.config ; \
  ln -s /home/${KAMEHOUSE_USERNAME}/docker /home/nbrest/docker

# Setup tomcat
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/tomcat/server.xml /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat/conf/
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/tomcat/tomcat-users.xml /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat/conf/
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/tomcat/manager.xml /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat/conf/Catalina/localhost/
COPY --chown=${KAMEHOUSE_USERNAME}:users docker/tomcat/host-manager.xml /home/${KAMEHOUSE_USERNAME}/programs/apache-tomcat/conf/Catalina/localhost/
# setup bin and sql folders
COPY --chown=${KAMEHOUSE_USERNAME}:users docker /home/${KAMEHOUSE_USERNAME}/docker
COPY --chown=${KAMEHOUSE_USERNAME}:users sql /home/${KAMEHOUSE_USERNAME}/sql
COPY --chown=${KAMEHOUSE_USERNAME}:users bin /home/${KAMEHOUSE_USERNAME}/bin

# Clone and deploy kamehouse
ARG RELEASE_VERSION=v0.10
RUN sudo su - ${KAMEHOUSE_USERNAME} -c "echo RELEASE_VERSION=${RELEASE_VERSION} >> /home/${KAMEHOUSE_USERNAME}/.env ; \
  mkdir -p /home/${KAMEHOUSE_USERNAME}/git ; \
  chmod a+xwr /home/${KAMEHOUSE_USERNAME}/git ; \
  rm -rf /home/${KAMEHOUSE_USERNAME}/git/kamehouse ; \
  cd /home/${KAMEHOUSE_USERNAME}/git ; \
  # Clone and deploy kamehouse
  git clone https://github.com/nbrest/kamehouse.git ; \
  cd /home/${KAMEHOUSE_USERNAME}/git/kamehouse ; \
  git checkout tags/${RELEASE_VERSION} -b ${RELEASE_VERSION} ; \
  git branch -D master ; \
  /home/${KAMEHOUSE_USERNAME}/bin/deploy-kamehouse.sh ; \
  ln -s /home/${KAMEHOUSE_USERNAME}/git/kamehouse /home/${KAMEHOUSE_USERNAME}/git/java.web.kamehouse ; \
  # clear temporary files
  /home/${KAMEHOUSE_USERNAME}/programs/apache-maven/bin/mvn clean ; \
  rm -rf /home/${KAMEHOUSE_USERNAME}/.m2 ; \
  /home/${KAMEHOUSE_USERNAME}/bin/create-sample-video-playlists.sh" ; \
  # intial dump of mysql data
  /home/${KAMEHOUSE_USERNAME}/bin/mysql-init.sh ; \
  ln -s /home/${KAMEHOUSE_USERNAME}/git /home/nbrest/git

# Set timezone (breaks webapps deployment on v6.04)
#ENV TZ=Australia/Sydney
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

CMD "/home/${KAMEHOUSE_USERNAME}/bin/init.sh"