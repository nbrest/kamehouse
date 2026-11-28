#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  PATH_DUMP_FILE=${HOME}/.kamehouse/backup/db/dump
  NUMBER_OF_EXPORTS=3
  SCHEMAS=(kamehouse kamehouse_tennisworld kamehouse_ztvrc kamehouse_testmodule)
  DUMP_FILENAMES=(dump-kamehouse.sql dump-kamehouse-tennisworld.sql dump-kamehouse-ztvrc.sql dump-kamehouse-testmodule.sql)
  LOG_FILENAMES=(dump-kamehouse.log dump-kamehouse-tennisworld.log dump-kamehouse-ztvrc.log dump-kamehouse-testmodule.log)
}

mainProcess() {
  setupInitialDirectories
  executeBackup
  cyclePreviousBackups
  listGeneratedFiles
}

setupInitialDirectories() {
  log.info "Creating backup directories if they don't exist"
  mkdir -v -p ${PATH_DUMP_FILE}/old
  chmod -R 700 ${HOME}/.kamehouse/backup
}

executeBackup() {
  log.info "Executing database backup"
  for i in "${!SCHEMAS[@]}"; do
    SCHEMA=${SCHEMAS[i]}
    DUMP_FILENAME=${DUMP_FILENAMES[i]}
    LOG_FILENAME=${LOG_FILENAMES[i]}
    log.info "Dumping schema ${SCHEMA} to ${DUMP_FILENAME}"
    mariadb-dump -v -i -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} ${SCHEMA} --dump-date --triggers --add-drop-database --add-drop-table --log-error=${PATH_DUMP_FILE}/${LOG_FILENAME}.tmp --result-file=${PATH_DUMP_FILE}/${DUMP_FILENAME}.tmp
    checkCommandStatus "$?"
    mv -v -f ${PATH_DUMP_FILE}/${LOG_FILENAME}.tmp ${PATH_DUMP_FILE}/${LOG_FILENAME}
    checkCommandStatus "$?"
    mv -v -f ${PATH_DUMP_FILE}/${DUMP_FILENAME}.tmp ${PATH_DUMP_FILE}/${DUMP_FILENAME}
    checkCommandStatus "$?"
  done
  log.info "mariadb dump commands executed successfully"
}

cyclePreviousBackups() {
  log.info "Cycling backups in old/ folder"
  for ((i = ${NUMBER_OF_EXPORTS} ; i >= 0 ; i--));
  do
    nextIndex=$(($i+1))
    for j in "${!DUMP_FILENAMES[@]}"; do
      DUMP_FILENAME=${DUMP_FILENAMES[j]}
      LOG_FILENAME=${LOG_FILENAMES[j]}
      if [ -f "${PATH_DUMP_FILE}/old/${DUMP_FILENAME}.$i" ]; then
        mv -v -f ${PATH_DUMP_FILE}/old/${DUMP_FILENAME}.$i ${PATH_DUMP_FILE}/old/${DUMP_FILENAME}.$nextIndex
      fi
      if [ -f "${PATH_DUMP_FILE}/old/${LOG_FILENAME}.$i" ]; then
        mv -v -f ${PATH_DUMP_FILE}/old/${LOG_FILENAME}.$i ${PATH_DUMP_FILE}/old/${LOG_FILENAME}.$nextIndex
      fi
    done
  done
  log.info "Copying latest backups to old/ folder"
  for j in "${!DUMP_FILENAMES[@]}"; do
    DUMP_FILENAME=${DUMP_FILENAMES[j]}
    LOG_FILENAME=${LOG_FILENAMES[j]}
    cp -v -f ${PATH_DUMP_FILE}/${DUMP_FILENAME} ${PATH_DUMP_FILE}/old/${DUMP_FILENAME}.0
    cp -v -f ${PATH_DUMP_FILE}/${LOG_FILENAME} ${PATH_DUMP_FILE}/old/${LOG_FILENAME}.0
  done
}

listGeneratedFiles() {
  log.info "Listing latest backup files"
  echo ${PATH_DUMP_FILE}
  ls -lh ${PATH_DUMP_FILE}
}

main "$@"
