#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  PATH_SQL=${HOME}/programs/kamehouse-shell/sql/mariadb
  REQUEST_CONFIRMATION_RX=^yes\|y$
  SKIP_CONFIRMATION=false
  ADD_DUMP_DATA=false
}

mainProcess() {
  requestConfirmation
  log.info "Setting up kamehouse database"
  if ${IS_LINUX_HOST}; then
    setupKameHouseLinux
  else
    setupKameHouseWindows
  fi
  log.info "Finished setting up kamehouse database"
}

setupKameHouseLinux() {
  setSudoKameHouseCommand "mariadb"

  log.info "Executing add-kamehouse-user.sql"
  ${SUDO_KAMEHOUSE_COMMAND} -e"set @kameHousePass = '${MARIADB_PASS_KAMEHOUSE}'; `cat ${PATH_SQL}/add-kamehouse-user.sql`"
  checkCommandStatus "$?" "Error running add-kamehouse-user.sql"

  log.info "Executing create-kamehouse-schema.sql"
  ${SUDO_KAMEHOUSE_COMMAND} -v < ${PATH_SQL}/create-kamehouse-schema.sql
  checkCommandStatus "$?" "Error running create-kamehouse-schema.sql"

  log.info "Executing spring-session.sql"
  ${SUDO_KAMEHOUSE_COMMAND} kamehouse < ${PATH_SQL}/spring-session.sql 
  checkCommandStatus "$?" "Error running spring-session.sql"

  if ${ADD_DUMP_DATA}; then
    log.info "Executing dump-kamehouse.sql"
    ${SUDO_KAMEHOUSE_COMMAND} kamehouse < ${PATH_SQL}/dump-kamehouse.sql 
    checkCommandStatus "$?" "Error running dump-kamehouse.sql"
  fi
}

setupKameHouseWindows() {
  log.info "Executing add-kamehouse-user.sql"
  mariadb -u root -p${MARIADB_PASS_ROOT_WIN} -e"set @kameHousePass = '${MARIADB_PASS_KAMEHOUSE}'; `cat ${PATH_SQL}/add-kamehouse-user.sql`"
  checkCommandStatus "$?" "Error running add-kamehouse-user.sql"

  log.info "Executing create-kamehouse-schema.sql"
  mariadb -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} -v < ${PATH_SQL}/create-kamehouse-schema.sql
  checkCommandStatus "$?" "Error running create-kamehouse-schema.sql"

  log.info "Executing spring-session.sql"
  mariadb -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} -v kamehouse < ${PATH_SQL}/spring-session.sql 
  checkCommandStatus "$?" "Error running spring-session.sql"

  if ${ADD_DUMP_DATA}; then
    log.info "Executing dump-kamehouse.sql"
    mariadb -u kamehouse -p${MARIADB_PASS_KAMEHOUSE} -v kamehouse < ${PATH_SQL}/dump-kamehouse.sql 
    checkCommandStatus "$?" "Error running dump-kamehouse.sql"
  fi
}

requestConfirmation() {
  if ! ${SKIP_CONFIRMATION}; then
    log.warn "${COL_YELLOW}This process will reset the data in the kamehouse database"
    log.info "Do you want to proceed? (${COL_BLUE}Yes${COL_DEFAULT_LOG}/${COL_RED}No${COL_DEFAULT_LOG}):"
    read SHOULD_PROCEED
    SHOULD_PROCEED=`echo "${SHOULD_PROCEED}" | tr '[:upper:]' '[:lower:]'`
    if [[ "${SHOULD_PROCEED}" =~ ${REQUEST_CONFIRMATION_RX} ]]; then
      log.info "Proceeding"
    else
      log.warn "${COL_PURPLE}${SCRIPT_NAME}${COL_DEFAULT_LOG} cancelled by the user"
      exitProcess ${EXIT_PROCESS_CANCELLED}
    fi
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
      -d)
        ADD_DUMP_DATA=true
        ;;
      -s)
        SKIP_CONFIRMATION=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-d" "import initial dump data"
  addHelpOption "-s" "skip confirmation check. for use in automated scripts"
}

main "$@"



