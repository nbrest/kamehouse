#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Troubleshoot issues:
# - Start mysql server with secure-file-priv="" (my.ini or my.conf)
# - Make sure ${PATH_CSV} is writable by everyone in windows

source ${HOME}/my.scripts/.cred/.cred

# Global variables
LOG_PROCESS_TO_FILE=true
PATH_CSV=${HOME}/home-synced/mysql/csv
NUMBER_OF_BACKUPS=3

mainProcess() {
  setupInitialDirectories
  executeExport
  cyclePreviousExports
  listGeneratedFiles
}

setupInitialDirectories() {
  log.info "Creating export directories if they don't exist"
  mkdir -v -p ${PATH_CSV}/old
  if ${IS_LINUX_HOST}; then
    log.info "Cleaning up /tmp csv files"
    sudo chown ${USER}:${USER} /tmp/*.tmpcsv
    rm -v -f /tmp/*.tmpcsv
  fi
}

executeExport() {
  log.info "Exporting kamehouse database to csv"
  if ${IS_LINUX_HOST}; then
    PATH_SQL=${HOME}/my.scripts/lin/sql/mysql
  else
    PATH_SQL=${HOME}/my.scripts/win/sql/mysql
  fi
  mysql -u nikolqs -p${MYSQL_PASS_NIKOLQS} < ${PATH_SQL}/csv-kamehouse.sql
  checkCommandStatus "$?"
  if ${IS_LINUX_HOST}; then
    log.info "Moving generated csv files from /tmp to ${PATH_CSV}"
    sudo chown ${USER}:${USER} /tmp/*.tmpcsv
    checkCommandStatus "$?"
    mv -v -f /tmp/*.tmpcsv ${PATH_CSV}
    checkCommandStatus "$?"
  fi
  for TMPCSV_FILE in ${PATH_CSV}/*.tmpcsv; do
    TMPCSV_FILENAME="$(basename -- ${TMPCSV_FILE})"
    TMPCSV_FILENAME="${TMPCSV_FILENAME%.*}"
    mv -v -f ${TMPCSV_FILE} ${PATH_CSV}/${TMPCSV_FILENAME}.csv
  done
}

cyclePreviousExports() {
  log.info "Cycling exports in old/ folder"
  for ((i = ${NUMBER_OF_BACKUPS} ; i >= 0 ; i--)); do  
    nextIndex=$(($i+1))
    for CSV_FILE in ${PATH_CSV}/old/*.csv.$i; do
      CSV_FILENAME="$(basename -- $CSV_FILE)"
      CSV_FILENAME="${CSV_FILENAME%.*}"
      if [ -f "${CSV_FILE}" ]; then
        mv -v -f ${CSV_FILE} ${PATH_CSV}/old/${CSV_FILENAME}.$nextIndex
      fi
    done
  done
  log.info "Copying latest export to old/ folder"
  for CSV_FILE in ${PATH_CSV}/*.csv; do
    CSV_FILENAME="$(basename -- $CSV_FILE)"
    cp -v -f ${CSV_FILE} ${PATH_CSV}/old/${CSV_FILENAME}.0
  done
}

listGeneratedFiles() {
  log.info "Listing latest backup files"
  echo ${PATH_CSV}
  ls -lh ${PATH_CSV}
}

main "$@"
