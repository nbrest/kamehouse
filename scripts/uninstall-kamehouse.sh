#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  PURGE_CONFIG=false
  KAMEHOUSE_SHELL_ONLY=false
}

mainProcessPre() {
  log.info "Uninstalling ${COL_PURPLE}kamehouse"

  revertBashRc
  deleteKameHouseShell

  if ${KAMEHOUSE_SHELL_ONLY}; then
    log.info "Uninstalling only kamehouse-shell. Skipping the rest of the steps"
  else 
    deleteTomcatWebapps
    deleteKameHouseCmd
    deleteKameHouseUiStaticFiles
    deleteKameHouseGroot
    deleteKameHouseGit
    purgeConfigFiles
  
    log.info "This script doesn't remove the database contents. To do that, login to mariadb and execute 'DROP SCHEMA IF EXISTS kamehouse;'"
  fi

  log.info "Finished uninstalling ${COL_PURPLE}kamehouse"
}

revertBashRc() {
  log.info "Reverting ${HOME}/.bashrc"
  sed -i "s#source \${HOME}/programs/kamehouse-shell/bin/bashrc/bashrc.sh##Ig" "${HOME}/.bashrc"
}

deleteTomcatWebapps() {
  log.info "Deleting tomcat webapps"
  rm -r -f ${HOME}/programs/apache-tomcat/webapps/kame-house*
}

deleteKameHouseShell() {
  log.info "Deleting kamehouse shell scripts"
  rm -r -f ${HOME}/programs/kamehouse-shell
}

deleteKameHouseCmd() {
  log.info "Deleting kamehouse cmd"
  rm -r -f ${HOME}/programs/kamehouse-cmd
}

deleteKameHouseUiStaticFiles() {
  log.info "Deleting kamehouse ui static files"
  if [ -d "/var/www/kamehouse-webserver" ]; then
    rm -rf /var/www/kamehouse-webserver/kame-house
  fi
  
  if [ -d "${HOME}/programs/apache-httpd/www/kamehouse-webserver" ]; then
    rm -rf ${HOME}/programs/apache-httpd/www/kamehouse-webserver/kame-house
  fi
}

deleteKameHouseGroot() {
  log.info "Deleting kamehouse groot"
  if [ -d "/var/www/kamehouse-webserver" ]; then
    rm -rf /var/www/kamehouse-webserver/kame-house-groot
  fi
  
  if [ -d "${HOME}/programs/apache-httpd/www/kamehouse-webserver" ]; then
    rm -rf ${HOME}/programs/apache-httpd/www/kamehouse-webserver/kame-house-groot
  fi

  if [ -f "/var/www/.kamehouse-server" ]; then
    rm -rf /var/www/.kamehouse-server
  fi  
}

deleteKameHouseGit() {
  log.info "Deleting kamehouse git repository"
  rm -r -f ${HOME}/git/kamehouse
}

purgeConfigFiles() {
  if ${PURGE_CONFIG}; then
    log.info "Deleting all config files"
    rm -r -f -v ${HOME}/.kamehouse/config/keys
  else
    log.info "Running without -p so skipping purging config files"
  fi
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -p)
        PURGE_CONFIG=true
        ;;
      -s)
        KAMEHOUSE_SHELL_ONLY=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done 
}

printHelpOptions() {
  addHelpOption "-p" "purge config files as well"
  addHelpOption "-s" "uninstall kamehouse-shell only"
}

main "$@"
