#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo "$(date +%Y-%m-%d' '%H:%M:%S) - [ERROR] - An error occurred importing common-functions.sh"
	exit 99
fi
# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 99
fi
source ${HOME}/programs/kamehouse-shell/bin/common/backup/backup-server-functions.sh
if [ "$?" != "0" ]; then
	echo "$(date +%Y-%m-%d' '%H:%M:%S) - [ERROR] - An error occurred importing backup-server-functions.sh"
	exit 99
fi

MARIADB_VERSION_WIN="10.11"

backupApacheHttpd() {
	log.info "Backing up apache-httpd"  
  local SOURCE_HTTPD=${HOME}/programs/apache-httpd
  local DEST_HTTPD=${PROJECT_DIR}/${HOSTNAME}${HOME}/programs/apache-httpd
  mkdir -p ${DEST_HTTPD}/htdocs
  checkCommandStatus "$?" "An error occurred creating directories"
  # -P to skip symlinks otherwise it fails copying kame-house symlink
  cp -vrf ${SOURCE_HTTPD}/*.txt ${DEST_HTTPD}/
  checkCommandStatus "$?" "An error occurred during file copy"
  cp -vrf ${SOURCE_HTTPD}/bin ${DEST_HTTPD}/
  checkCommandStatus "$?" "An error occurred during file copy"
  cp -vrf ${SOURCE_HTTPD}/cgi-bin ${DEST_HTTPD}/
  checkCommandStatus "$?" "An error occurred during file copy"   
  cp -vrf ${SOURCE_HTTPD}/conf ${DEST_HTTPD}/
  checkCommandStatus "$?" "An error occurred during file copy" 
  cp -vrf ${SOURCE_HTTPD}/error ${DEST_HTTPD}/
  checkCommandStatus "$?" "An error occurred during file copy" 
  cp -vrf ${SOURCE_HTTPD}/htdocs/*.html ${DEST_HTTPD}/htdocs/
  checkCommandStatus "$?" "An error occurred during file copy" 
  cp -vrf ${SOURCE_HTTPD}/htdocs/*.ico ${DEST_HTTPD}/htdocs/
  checkCommandStatus "$?" "An error occurred during file copy" 
  cp -vrf ${SOURCE_HTTPD}/icons ${DEST_HTTPD}/
  checkCommandStatus "$?" "An error occurred during file copy" 
  cp -vrf ${SOURCE_HTTPD}/include ${DEST_HTTPD}/
  checkCommandStatus "$?" "An error occurred during file copy" 
  cp -vrf ${SOURCE_HTTPD}/lib ${DEST_HTTPD}/
  checkCommandStatus "$?" "An error occurred during file copy" 
  cp -vrf ${SOURCE_HTTPD}/logs ${DEST_HTTPD}/
  checkCommandStatus "$?" "An error occurred during file copy" 
  cp -vrf ${SOURCE_HTTPD}/modules ${DEST_HTTPD}/
  checkCommandStatus "$?" "An error occurred during file copy" 
  
  rm -rfv ${DEST_HTTPD}/logs/*
  checkCommandStatus "$?" "An error occurred during file copy"
  echo "" > ${DEST_HTTPD}/logs/empty_log.txt
  checkCommandStatus "$?" "An error occurred during file copy"
}

backupMariadbConfig() {
  log.info "Backing up mariadb config"
  mkdir -p ${PROJECT_DIR}/${HOSTNAME}/mariadb-config/
  checkCommandStatus "$?" "An error occurred creating directories"
  # Doesn't work if I double quote MARIADB_INI in the definition
  local MARIADB_INI="/c/Program Files/MariaDB ${MARIADB_VERSION_WIN}/data/my.ini"
  if test -f "${MARIADB_INI}"; then
    cp -vrf "${MARIADB_INI}" ${PROJECT_DIR}/${HOSTNAME}/mariadb-config/
	  checkCommandStatus "$?" "An error occurred during file copy"
  else
    log.warn "${MARIADB_INI} doesn't exist"
  fi  
}

backupStartupScripts() {
  log.info "Backing up startup scripts"
  # Doesn't work if I double quote the path
  local USERNAME=`whoami`
  local STARTUP_SCRIPTS_PATH=/c/Users/${USERNAME}/AppData/Roaming/Microsoft/Windows/Start\ Menu/Programs/Startup
  local STARTUP_SCRIPTS_DEST="/c/Users/${USERNAME}/AppData/Roaming/Microsoft/Windows/Start_Menu/Programs/Startup"
  local DEST_PATH=${PROJECT_DIR}/${HOSTNAME}${STARTUP_SCRIPTS_DEST}
  mkdir -p ${DEST_PATH}
  checkCommandStatus "$?" "An error occurred creating directories"
  
  if test -d "${STARTUP_SCRIPTS_PATH}"; then
    cp -vrf "${STARTUP_SCRIPTS_PATH}" ${DEST_PATH}/
	  checkCommandStatus "$?" "An error occurred during file copy"
  else
    log.warn "${STARTUP_SCRIPTS_PATH} doesn't exist"
  fi
}

customBackupTask() {
  backupPhpConfig
}

backupPhpConfig() {
  log.info "Backing up php config"
  mkdir -p ${PROJECT_DIR}/${HOSTNAME}/php-config/
  checkCommandStatus "$?" "An error occurred creating directories"
  # Doesn't work if I double quote PHP_INI in the definition
  local PHP_INI=${HOME}/programs/php/php.ini
  if test -f "${PHP_INI}"; then
    cp -vrf "${PHP_INI}" ${PROJECT_DIR}/${HOSTNAME}/php-config/
	  checkCommandStatus "$?" "An error occurred during file copy"
  else
    log.warn "${PHP_INI} doesn't exist"
  fi
}

main "$@"
