# Common functions and variables in backup-server.sh scripts

# Global variables
LOG_PROCESS_TO_FILE=true
GIT_BRANCH="dev"
GIT_REMOTE="all"
PROJECT_DIR="${HOME}/git/kamehouse-server-config"
DOCKER_PORT_SSH=7022

mainProcess() {
  # Set in DEST_HOME mainProcess because HOSTNAME is overriden for aws
  DEST_HOME=${PROJECT_DIR}/${HOSTNAME}${HOME}

  pullChangesFromGit
  resettingBackupDir
  exportMysqlData
  backupApacheHttpd
  backupTomcat
  backupTomcatDev
  backupMysqlConfig
  backupHomeFiles
  backupHomeFolders
  backupStartupScripts
  backupWorkspaceEclipse
  backupWorkspaceIntellij
  customBackupTask
  if ${IS_LINUX_HOST}; then
    backupRootFiles
    backupCrontabs
    backupEtc
    changeBackupPermissions
  fi
  pushChangesToGit
}

backupStartupScripts() {
  log.warn "Default backupStartupScripts(). Override in each script when other specific backup tasks are required."
}

customBackupTask() {
  log.warn "Default customBackupTask(). Override in each script when other specific backup tasks are required."
}

pullChangesFromGit() {
  gitCdCheckoutAndPull "${PROJECT_DIR}" ${GIT_REMOTE} ${GIT_BRANCH}
}

resettingBackupDir() {
  log.info "Resetting backup directory"
  if [ -z ${HOSTNAME} ]; then
    log.error "HOSTNAME variable not set. Can't continue"
    exitProcess 1
  fi
  if [ -z ${PROJECT_DIR} ]; then
    log.error "PROJECT_DIR variable not set. Can't continue"
    exitProcess 1
  fi
  rm -rvf ${PROJECT_DIR}/${HOSTNAME}
  local REMOVE_RESPONSE=$?
  if [ "${REMOVE_RESPONSE}" != "0" ]; then
    if ${IS_LINUX_HOST}; then
      # Try as root
      sudo rm -rvf ${PROJECT_DIR}/${HOSTNAME}
      sudo rm -rf ${PROJECT_DIR}/${HOSTNAME}/etc
    else
      # Try once again. Usually worked on windows
      rm -rvf ${PROJECT_DIR}/${HOSTNAME}
    fi
    REMOVE_RESPONSE=$?
  fi
  checkCommandStatus "${REMOVE_RESPONSE}" "An error occurred resetting directories"
}

exportMysqlData() {
	log.info "Exporting mysql data from mysql server"
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/mysql-csv-kamehouse.sh
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/mysql-dump-kamehouse.sh
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/mysql-dump-kamehouse-docker.sh -p prod
}

backupApacheHttpd() {
  log.info "Backing up apache http config"
  mkdir -p ${PROJECT_DIR}/${HOSTNAME}/etc/apache2
  checkCommandStatus "$?" "An error occurred creating directories"
  sudo cp -vrf /etc/apache2/* ${PROJECT_DIR}/${HOSTNAME}/etc/apache2/
  checkCommandStatus "$?" "An error occurred during file copy"
}

backupTomcat() {
  log.info "Backing up apache-tomcat"
  local SOURCE_TOMCAT=${HOME}/programs/apache-tomcat
  local DEST_TOMCAT=${PROJECT_DIR}/${HOSTNAME}${HOME}/programs/apache-tomcat
  copyTomcatFolders "${SOURCE_TOMCAT}" "${DEST_TOMCAT}"
}

backupTomcatDev() {
  log.info "Backing up apache-tomcat-dev"
  local SOURCE_TOMCAT=${HOME}/programs/apache-tomcat-dev
  local DEST_TOMCAT=${PROJECT_DIR}/${HOSTNAME}${HOME}/programs/apache-tomcat-dev
  copyTomcatFolders "${SOURCE_TOMCAT}" "${DEST_TOMCAT}"
}

copyTomcatFolders() {
  local SOURCE_TOMCAT=$1
  local DEST_TOMCAT=$2

  if [ -d "${SOURCE_TOMCAT}" ]; then
    mkdir -p ${DEST_TOMCAT}/bin
    checkCommandStatus "$?" "An error occurred creating directories"

  	cp -vrf ${SOURCE_TOMCAT}/conf ${DEST_TOMCAT}/
  	checkCommandStatus "$?" "An error occurred during file copy"
    cp -vrf ${SOURCE_TOMCAT}/bin/*.sh ${DEST_TOMCAT}/bin
    checkCommandStatus "$?" "An error occurred during file copy"
    cp -vrf ${SOURCE_TOMCAT}/bin/*.bat ${DEST_TOMCAT}/bin
    checkCommandStatus "$?" "An error occurred during file copy"
  else 
    log.warn "${SOURCE_TOMCAT} doesn't exist"
  fi
}

backupHomeFiles() {
  log.info "Backing up home dir files"  
  mkdir -p ${DEST_HOME}
  checkCommandStatus "$?" "An error occurred creating directories"
  cp -vrf ${HOME}/.bashrc ${DEST_HOME}/.bashrc
  checkCommandStatus "$?" "An error occurred during file copy"
  cp -vrf ${HOME}/.profile ${DEST_HOME}/.profile
  checkCommandStatus "$?" "An error occurred during file copy"
}

backupHomeFolders() {
  log.info "Backing up ${USER} home dir folders"
  pullDockerHomeFolders

  if ${IS_LINUX_HOST}; then
    sudo cp -vrf ${HOME}/home-synced ${DEST_HOME}/
  else
    cp -vrf ${HOME}/home-synced ${DEST_HOME}/
  fi
  checkCommandStatus "$?" "An error occurred during file copy"

  if ${IS_LINUX_HOST}; then
    sudo cp -vrf ${HOME}/.kamehouse ${DEST_HOME}/
  else
    cp -vrf ${HOME}/.kamehouse ${DEST_HOME}/
  fi
  checkCommandStatus "$?" "An error occurred during file copy"
}

pullDockerHomeFolders() {
  log.info "Pulling folders to sync from docker prod container, if it's running"
  scp -r -P ${DOCKER_PORT_SSH} localhost:/home/${DOCKER_USERNAME}/home-synced ${HOME}/home-synced/docker/ 
  scp -r -P ${DOCKER_PORT_SSH} localhost:/home/${DOCKER_USERNAME}/.kamehouse ${HOME}/home-synced/docker/ 
  rm -f ${HOME}/home-synced/docker/.kamehouse/.kamehouse-docker-container-env
}

backupWorkspaceEclipse() {
  log.info "Backing up eclipse config folders"
  local SOURCE_HTTPD_ECLIPSE=${HOME}/programs/apache-httpd/www/www-eclipse
  if ${IS_LINUX_HOST}; then
    SOURCE_HTTPD_ECLIPSE=/var/www/www-eclipse
  fi
  local DEST_HTTPD_ECLIPSE=${DEST_HOME}/www-eclipse
  copyApacheDevFolders "${SOURCE_HTTPD_ECLIPSE}" "${DEST_HTTPD_ECLIPSE}"
}

backupWorkspaceIntellij() {
  log.info "Backing up intellij config folders"
  local SOURCE_HTTPD_INTELLIJ=${HOME}/programs/apache-httpd/www/www-intellij
  if ${IS_LINUX_HOST}; then
    SOURCE_HTTPD_INTELLIJ=/var/www/www-intellij
  fi
  local DEST_HTTPD_INTELLIJ=${DEST_HOME}/www-intellij
  copyApacheDevFolders "${SOURCE_HTTPD_INTELLIJ}" "${DEST_HTTPD_INTELLIJ}"
}

copyApacheDevFolders() {
  local SOURCE_WWW=$1
  local DEST_WWW=$2

  if [ -d "${SOURCE_WWW}" ]; then
    mkdir -p ${DEST_WWW}
    cp -vrf ${SOURCE_WWW}/*.html ${DEST_WWW}
    checkCommandStatus "$?" "An error occurred during file copy" 
  else
    log.warn "${SOURCE_WWW} doesn't exist"
  fi
}

backupMysqlConfig() {
	log.info "Backing up mysql config"
  mkdir -p ${PROJECT_DIR}/${HOSTNAME}/etc/mysql/
	sudo cp -vrf /etc/mysql/* ${PROJECT_DIR}/${HOSTNAME}/etc/mysql/
  checkCommandStatus "$?" "An error occurred during file copy"
}

backupRootFiles() {
  log.info "Backing up root files"
  mkdir -p ${PROJECT_DIR}/${HOSTNAME}${HOME}
  checkCommandStatus "$?" "An error occurred creating directories"
  sudo cp -vrf /root/.bashrc ${DEST_HOME}/.bashrc.root
  checkCommandStatus "$?" "An error occurred during file copy"
  sudo cp -vrf /root/.profile ${DEST_HOME}/.profile.root
  checkCommandStatus "$?" "An error occurred during file copy"
}

backupCrontabs() {
  log.info "Backing up crontabs"
  crontab -l | tee ${DEST_HOME}/crontab.${USER}
  checkCommandStatus "$?" "An error occurred during crontab export"
  sudo crontab -l | tee ${DEST_HOME}/crontab.root
  checkCommandStatus "$?" "An error occurred during crontab export"
}

backupEtc() {
  # /etc/mysql and /etc/apache2 already backedup

  log.info "Backing up etc"
  mkdir -p ${PROJECT_DIR}/${HOSTNAME}/etc
  checkCommandStatus "$?" "An error occurred creating directories"

  sudo cp -vrf /etc/hosts ${PROJECT_DIR}/${HOSTNAME}/etc/hosts
  checkCommandStatus "$?" "An error occurred during file copy"

  sudo cp -vrf /etc/fstab ${PROJECT_DIR}/${HOSTNAME}/etc/fstab
  checkCommandStatus "$?" "An error occurred during file copy"

  sudo cp -vrf /etc/sudoers ${PROJECT_DIR}/${HOSTNAME}/etc/sudoers
  checkCommandStatus "$?" "An error occurred during file copy"

  if test -d "/etc/default"; then
    sudo rm -rf ${PROJECT_DIR}/${HOSTNAME}/etc/default
    sudo cp -vrf /etc/default ${PROJECT_DIR}/${HOSTNAME}/etc/default
	  checkCommandStatus "$?" "An error occurred during file copy"
  fi

  if test -d "/etc/letsencrypt"; then
    sudo rm -rf ${PROJECT_DIR}/${HOSTNAME}/etc/letsencrypt
    sudo cp -vrf /etc/letsencrypt ${PROJECT_DIR}/${HOSTNAME}/etc/letsencrypt
	  checkCommandStatus "$?" "An error occurred during file copy"
  fi

  if test -d "/etc/php"; then
    sudo rm -rf ${PROJECT_DIR}/${HOSTNAME}/etc/php
    sudo cp -vrf /etc/php ${PROJECT_DIR}/${HOSTNAME}/etc/php
	  checkCommandStatus "$?" "An error occurred during file copy"
  fi
}

changeBackupPermissions() {
  log.info "Changing ownership of folders in git"
  sudo chown -v -R ${USER}:${USER} ${PROJECT_DIR}/${HOSTNAME}	
  #checkCommandStatus "$?" "An error occurred executing chown"
}

pushChangesToGit() {
  log.info "Completed synching ${HOSTNAME}" > ${DEST_HOME}/sync.log
  gitCdCommitAllChangesAndPush "${PROJECT_DIR}/${HOSTNAME}" ${GIT_REMOTE} ${GIT_BRANCH} "Backed up ${HOSTNAME} server config"
}
