#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi
loadKamehouseShellPwd

# Global variables
PATH_DUMP_FILE=${HOME}/.kamehouse/mariadb/dump 
NUMBER_OF_EXPORTS=3
DUMP_FILENAME=dump-kamehouse.sql
LOG_FILENAME=dump-kamehouse.log

mainProcess() {
  setupInitialDirectories
  executeBackup
  cyclePreviousBackups
  listGeneratedFiles
}

setupInitialDirectories() {
  log.info "Creating backup directories if they don't exist"
  mkdir -v -p ${PATH_DUMP_FILE}/old
  chmod -R 700 ${HOME}/.kamehouse
}

cyclePreviousBackups() {
  log.info "Cycling backups in old/ folder"
  for ((i = ${NUMBER_OF_EXPORTS} ; i >= 0 ; i--)); 
  do  
    nextIndex=$(($i+1))
    if [ -f "${PATH_DUMP_FILE}/old/${DUMP_FILENAME}.$i" ]; then
      mv -v -f ${PATH_DUMP_FILE}/old/${DUMP_FILENAME}.$i ${PATH_DUMP_FILE}/old/${DUMP_FILENAME}.$nextIndex
    fi
    if [ -f "${PATH_DUMP_FILE}/old/${LOG_FILENAME}.$i" ]; then
      mv -v -f ${PATH_DUMP_FILE}/old/${LOG_FILENAME}.$i ${PATH_DUMP_FILE}/old/${LOG_FILENAME}.$nextIndex
    fi
  done
  log.info "Copying latest backup to old/ folder"
  cp -v -f ${PATH_DUMP_FILE}/${DUMP_FILENAME} ${PATH_DUMP_FILE}/old/${DUMP_FILENAME}.0
  cp -v -f ${PATH_DUMP_FILE}/${LOG_FILENAME} ${PATH_DUMP_FILE}/old/${LOG_FILENAME}.0
}

executeBackup() {
  log.info "Executing database backup"
  mariadb-dump -v -i -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} kamehouse --dump-date --triggers --add-drop-database --add-drop-table --log-error=${PATH_DUMP_FILE}/${LOG_FILENAME}.tmp --result-file=${PATH_DUMP_FILE}/${DUMP_FILENAME}.tmp
  checkCommandStatus "$?"
  mv -v -f ${PATH_DUMP_FILE}/${LOG_FILENAME}.tmp ${PATH_DUMP_FILE}/${LOG_FILENAME}
  checkCommandStatus "$?"
  mv -v -f ${PATH_DUMP_FILE}/${DUMP_FILENAME}.tmp ${PATH_DUMP_FILE}/${DUMP_FILENAME}
  checkCommandStatus "$?"
  log.info "mariadb dump commands executed successfully"
}

listGeneratedFiles() {
  log.info "Listing latest backup files"
  echo ${PATH_DUMP_FILE}
  ls -lh ${PATH_DUMP_FILE}
}

main "$@"
