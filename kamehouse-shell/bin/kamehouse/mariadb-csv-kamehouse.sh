#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Troubleshoot issues:
# - Start mariadb server with secure-file-priv="" (my.ini or my.conf)
# - Make sure ${PATH_CSV} is writable by everyone in windows

source ${HOME}/.kamehouse/.shell/.cred

# Global variables
LOG_PROCESS_TO_FILE=true
PATH_CSV=${HOME}/.kamehouse/mariadb/csv
PATH_SQL=${HOME}/programs/kamehouse-shell/bin/kamehouse/sql/mariadb
NUMBER_OF_BACKUPS=3
OUT_FILE_BASE=""
TMP_EXPORT_DIR=/tmp/kamehouse-csv-${USER}

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
    mkdir -p ${TMP_EXPORT_DIR}
    chmod a+rwx ${TMP_EXPORT_DIR}
    log.info "Cleaning up ${TMP_EXPORT_DIR} csv files"
    chown ${USER}:${USER} ${TMP_EXPORT_DIR}/*.tmpcsv
    rm -v -f ${TMP_EXPORT_DIR}/*.tmpcsv
  fi
}

executeExport() {
  log.info "Exporting kamehouse database to csv"
  if ${IS_LINUX_HOST}; then
    OUT_FILE_BASE="${TMP_EXPORT_DIR}/"
  else
    OUT_FILE_BASE="C:/Users/"${USER}"/.kamehouse/mariadb/csv/"
  fi
  mariadb --force -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} --init-command="set @outFileBase = '${OUT_FILE_BASE}';" < ${PATH_SQL}/csv-kamehouse.sql
  if ${IS_LINUX_HOST}; then
    log.info "Moving generated csv files from ${TMP_EXPORT_DIR} to ${PATH_CSV}"
    chown ${USER}:${USER} ${TMP_EXPORT_DIR}/*.tmpcsv
    mv -v -f ${TMP_EXPORT_DIR}/*.tmpcsv ${PATH_CSV}
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
