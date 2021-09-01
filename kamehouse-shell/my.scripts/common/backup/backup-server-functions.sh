# Common functions and variables in backup-server.sh scripts

# Global variables
LOG_PROCESS_TO_FILE=true
GIT_BRANCH="dev"
PROJECT_DIR="${HOME}/git/java.web.kamehouse.private"

mainProcess() {
  # Set in DEST_HOME mainProcess because HOSTNAME is overriden for aws
  DEST_HOME=${PROJECT_DIR}/${HOSTNAME}${HOME}

  pullChangesFromGit
  resettingBackupDir
  exportMysqlData
  backupApacheHttpd
  backupTomcat
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
  gitCdCheckoutAndPull "${PROJECT_DIR}" "origin" ${GIT_BRANCH}
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
  ${HOME}/my.scripts/kamehouse/mysql-csv-kamehouse.sh
  ${HOME}/my.scripts/kamehouse/mysql-dump-kamehouse.sh
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

backupWorkspaceEclipse() {
  log.info "Backing up workspace-eclipse config folders"
  local SOURCE_WORKSPACE_ECLIPSE=${HOME}/workspace-eclipse
  local DEST_WORKSPACE_ECLIPSE=${DEST_HOME}/workspace-eclipse
  local SOURCE_TOMCAT=${SOURCE_WORKSPACE_ECLIPSE}/apache-tomcat
  local DEST_TOMCAT=${DEST_WORKSPACE_ECLIPSE}/apache-tomcat
  copyTomcatFolders "${SOURCE_TOMCAT}" "${DEST_TOMCAT}"
  copyWorkspaceApacheFolders "${SOURCE_WORKSPACE_ECLIPSE}" "${DEST_WORKSPACE_ECLIPSE}"
}

backupWorkspaceIntellij() {
  log.info "Backing up workspace-intellij config folders"
  local SOURCE_WORKSPACE_INTELLIJ=${HOME}/workspace-intellij
  local DEST_WORKSPACE_INTELLIJ=${DEST_HOME}/workspace-intellij
  local SOURCE_TOMCAT=${SOURCE_WORKSPACE_INTELLIJ}/apache-tomcat
  local DEST_TOMCAT=${DEST_WORKSPACE_INTELLIJ}/apache-tomcat
  copyTomcatFolders "${SOURCE_TOMCAT}" "${DEST_TOMCAT}"
  copyWorkspaceApacheFolders "${SOURCE_WORKSPACE_INTELLIJ}" "${DEST_WORKSPACE_INTELLIJ}"
}

copyWorkspaceApacheFolders() {
  local SOURCE_WORKSPACE=$1
  local DEST_WORKSPACE=$2
  local SOURCE_WWW=${SOURCE_WORKSPACE}/apache-httpd/www
  local DEST_WWW=${DEST_WORKSPACE}/apache-httpd/www

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
    sudo cp -vrf /etc/default ${PROJECT_DIR}/${HOSTNAME}/etc/default
	  checkCommandStatus "$?" "An error occurred during file copy"
  fi

  if test -d "/etc/letsencrypt"; then
    sudo cp -vrf /etc/letsencrypt ${PROJECT_DIR}/${HOSTNAME}/etc/letsencrypt
	  checkCommandStatus "$?" "An error occurred during file copy"
  fi

  if test -d "/etc/php"; then
    sudo cp -vrf /etc/php ${PROJECT_DIR}/${HOSTNAME}/etc/php
	  checkCommandStatus "$?" "An error occurred during file copy"
  fi
}

changeBackupPermissions() {
  log.info "Changing ownership of folders in git"
  sudo chown -v -R ${USER}:${USER} ${PROJECT_DIR}/${HOSTNAME}	
  checkCommandStatus "$?" "An error occurred executing chown"
}

pushChangesToGit() {
  log.info "Completed synching ${HOSTNAME}" > ${DEST_HOME}/sync.log
  gitCdCommitAllChangesAndPush "${PROJECT_DIR}" "origin" ${GIT_BRANCH} "Backed up ${HOSTNAME} server config"
}
