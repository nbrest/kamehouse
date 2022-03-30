# BUILD: docker-build-kamehouse.sh : build's the docker image from this file
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
  apt-get install -y openjdk-11-jdk ; \
  apt-get install -y mariadb-server ; \
  apt-get install -y maven ; \
  apt-get install -y net-tools ; \
  apt-get install -y openssh-server ; \
  apt-get install -y php libapache2-mod-php ; \
  apt-get install -y screen ; \
  apt-get install -y sudo ; \
  apt-get install -y tightvncserver ; \
  apt-get install -y vim ; \
  apt-get install -y vlc ; \
  apt-get install -y zip ; \
  apt-get autopurge -y ; \
  apt-get autoclean -y ; \
  apt-get clean -y

# Setup apache httpd
COPY docker/apache2/conf /etc/apache2/conf
COPY docker/apache2/sites-available /etc/apache2/sites-available
COPY docker/apache2/certs/apache-selfsigned.crt /etc/ssl/certs/
COPY docker/apache2/certs/apache-selfsigned.key /etc/ssl/private/
COPY docker/apache2/robots.txt /var/www/html/
RUN ln -s /var/www/html/ /var/www/kh.webserver ; \
  a2ensite default-ssl ; \
  a2enmod headers proxy proxy_http proxy_wstunnel ssl rewrite 

# Setup users
COPY docker/etc/sudoers /etc/sudoers
RUN adduser --gecos "" --disabled-password nbrest ; \
  echo 'nbrest:nbrest' | chpasswd

# Setup nbrest home
RUN echo "source /home/nbrest/programs/kamehouse-shell/bin/lin/bashrc/bashrc.sh" >> /root/.bashrc ; \
  echo "source /home/nbrest/.kamehouse/.kamehouse-docker-container-env" >> /root/.bashrc ; \
  sudo su - nbrest -c "echo \"source /home/nbrest/programs/kamehouse-shell/bin/lin/bashrc/bashrc.sh\" >> /home/nbrest/.bashrc ; \
    echo \"source /home/nbrest/.kamehouse/.kamehouse-docker-container-env\" >> /home/nbrest/.bashrc ; \
    mkdir -p /home/nbrest/.ssh"

# Install tomcat
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/programs ; \
  cd /home/nbrest/programs ; \
  wget --no-check-certificate https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.53/bin/apache-tomcat-9.0.53.tar.gz ; \
  tar -xf /home/nbrest/programs/apache-tomcat-9.0.53.tar.gz -C /home/nbrest/programs/ ; \
  mv /home/nbrest/programs/apache-tomcat-9.0.53 /home/nbrest/programs/apache-tomcat ; \
  rm /home/nbrest/programs/apache-tomcat-9.0.53.tar.gz ; \
  sed -i \"s#localhost:8000#0.0.0.0:8000#g\" /home/nbrest/programs/apache-tomcat/bin/catalina.sh"
COPY --chown=nbrest:users docker/tomcat/server.xml /home/nbrest/programs/apache-tomcat/conf/
COPY --chown=nbrest:users docker/tomcat/tomcat-users.xml /home/nbrest/programs/apache-tomcat/conf/
COPY --chown=nbrest:users docker/tomcat/manager.xml /home/nbrest/programs/apache-tomcat/conf/Catalina/localhost/
COPY --chown=nbrest:users docker/tomcat/host-manager.xml /home/nbrest/programs/apache-tomcat/conf/Catalina/localhost/

# Increment number in the next command to trigger executing all the following layers instead of getting them from cache
# Clone KameHouse dev branch
RUN sudo su - nbrest -c "echo 'Update number to avoid cache 3' ; mkdir -p /home/nbrest/git ; \
  chmod a+xwr /home/nbrest/git ; \
  rm -rf /home/nbrest/git/kamehouse ; \
  cd /home/nbrest/git ; \
  git clone https://github.com/nbrest/kamehouse.git ; \
  cd /home/nbrest/git/kamehouse ; \
  git checkout dev ; \
  git branch -D master"

# Build kamehouse to download all the maven dependencies (then clean the target directories)
RUN sudo su - nbrest -c "cd /home/nbrest/git/kamehouse ; \
  mvn clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true ; \
  mvn clean ; \
  rm -rf /home/nbrest/.m2/repository/com/nicobrest"

################## Setup directories ################################
# /home/nbrest/.config/vlc
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/.config/vlc/"
COPY --chown=nbrest:users docker/vlc/* /home/nbrest/.config/vlc/

# /home/nbrest/home-synced
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/home-synced/.kamehouse/keys ; \
  mkdir -p /home/nbrest/home-synced/httpd ; \
  cp /home/nbrest/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 /home/nbrest/home-synced/.kamehouse/keys/kamehouse.pkcs12 ; \
  cp /home/nbrest/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt /home/nbrest/home-synced/.kamehouse/keys/kamehouse.crt"
COPY --chown=nbrest:users docker/keys/integration-test-cred.enc /home/nbrest/home-synced/.kamehouse/
COPY --chown=nbrest:users docker/apache2/.htpasswd /home/nbrest/home-synced/httpd
RUN ln -s /home/nbrest/home-synced/httpd/.htpasswd /var/www/html/.htpasswd

# /home/nbrest/.kamehouse/
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/.kamehouse"
COPY --chown=nbrest:users docker/keys/.vnc.server.pwd.enc /home/nbrest/.kamehouse/
COPY --chown=nbrest:users docker/keys/.unlock.screen.pwd.enc /home/nbrest/.kamehouse/

# /home/nbrest/logs
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/logs" ; \
  mkdir -p /root/logs

# /home/nbrest/programs/kamehouse-shell/bin
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/.kamehouse/.shell/" ; \ 
  ln -s /home/nbrest/programs /root/programs ; \
  ln -s /home/nbrest/.kamehouse /root/.kamehouse
COPY --chown=nbrest:users docker/keys/.cred /home/nbrest/.kamehouse/.shell/.cred

# /home/nbrest/programs
RUN sudo su - nbrest -c "mkdir -p /home/nbrest/programs/apache-httpd ; \
  mkdir -p /home/nbrest/programs/kamehouse-cmd/bin ; \
  mkdir -p /home/nbrest/programs/kamehouse-cmd/lib" ; \
  chmod a+rx /var/log/apache2 ; \
  ln -s /var/log/apache2 /home/nbrest/programs/apache-httpd/logs

# Kamehouse ui and groot static content:
RUN ln -s /home/nbrest/git/kamehouse/kamehouse-ui/src/main/webapp /var/www/html/kame-house ; \
  ln -s /home/nbrest/git/kamehouse/kamehouse-groot/public/kame-house-groot /var/www/html/kame-house-groot ; \
  rm /var/www/html/index.html ; \
  ln -s /home/nbrest/git/kamehouse/kamehouse-groot/public/index.html /var/www/html/index.html

#####################################################################

# Setup mocked bins
RUN mv /usr/bin/vlc /usr/bin/vlc-bin
COPY docker/mocked-bin/vlc /usr/bin/vlc
COPY docker/mocked-bin/vncdo /usr/local/bin/vncdo
COPY docker/mocked-bin/gnome-screensaver-command /usr/bin/gnome-screensaver-command
RUN chmod a+x /usr/bin/vlc ; \
  chmod a+x /usr/local/bin/vncdo ; \
  chmod a+x /usr/bin/gnome-screensaver-command

# Open mysqldb to external connections and intial dump of mysql data
RUN sed -i "s#bind-address            = 127.0.0.1#bind-address            = 0.0.0.0#g" /etc/mysql/mariadb.conf.d/50-server.cnf ; \
  service mysql start ; \
  sleep 5 ; \
  mysql < /home/nbrest/git/kamehouse/kamehouse-shell/bin/kamehouse/sql/mysql/setup-kamehouse.sql ; \
  mysql kameHouse < /home/nbrest/git/kamehouse/kamehouse-shell/bin/kamehouse/sql/mysql/spring-session.sql ; \
  mysql kameHouse < /home/nbrest/git/kamehouse/docker/mysql/dump-kamehouse.sql ; \
  cd /var/lib ; \
  tar -cvpzf /home/nbrest/mysql-initial-data.tar.gz mysql/ ; \
  chown nbrest:users /home/nbrest/mysql-initial-data.tar.gz

# Increment number in the next command to trigger executing all the following layers instead of getting them from cache
RUN echo "echo 'Update number to avoid cache 20'"

# Copy docker setup folder
COPY --chown=nbrest:users docker /home/nbrest/docker

# Deploy latest version of kamehouse (should have most of the dependencies already downloaded)
# Also updates the kamehouse-shell directory with the latest version of the scripts
# And recreate sample video playlists directories
RUN sudo su - nbrest -c "cd /home/nbrest/git/kamehouse ; \
  git pull origin dev ; \
  chmod a+x /home/nbrest/git/kamehouse/kamehouse-shell/bin/kamehouse/kamehouse-shell-install.sh ; \
  /home/nbrest/programs/kamehouse-shell/bin/kamehouse/deploy-kamehouse.sh -f -p docker ; \
  cd /home/nbrest/git/kamehouse ; \
  mvn clean ; \
  rm -rf /home/nbrest/.m2/repository/com/nicobrest ; \
  /home/nbrest/programs/kamehouse-shell/bin/kamehouse/create-sample-video-playlists.sh"

# Expose ports
EXPOSE 22 80 443 3306 8000 8080 9090

# Set timezone
ENV TZ=Australia/Sydney
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Setup default env for container init script
ENV FAST_DOCKER_INIT=false

CMD ["/home/nbrest/programs/kamehouse-shell/bin/kamehouse/docker/docker-init-kamehouse.sh"]