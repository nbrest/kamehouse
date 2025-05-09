#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing docker-functions.sh" ; exit 99
fi

initScriptEnv() {
  DATA_SOURCE="none"
  REQUEST_CONFIRMATION_RX=^yes\|y$
  REINIT_SSH_KEYS_ONLY=false
  REINIT_KAMEHOUSE_FOLDER_ONLY=false
  DOCKER_HOST_USERHOME=""
  SSH_OPTIONS="-vvv"
  SCP_OPTIONS="-vvv -3 -r"
}

mainProcess() {
  printReinitSettings

  requestConfirmation
  if ${REINIT_SSH_KEYS_ONLY}; then
    reinitSsh
    showContainerFolderStatus
    exitSuccessfully
  fi

  if ${REINIT_KAMEHOUSE_FOLDER_ONLY}; then
    reinitKameHouseFolder
    showContainerFolderStatus
    exitSuccessfully
  fi

  reinitSsh
  reinitKameHouseFolder
  reinitMariadb
  showContainerFolderStatus
}

printReinitSettings() {
  log.info "Reinit folders and data in the docker kamehouse container from host ${COL_PURPLE}${DOCKER_HOST_IP}${COL_DEFAULT_LOG} and remote user ${COL_PURPLE}${DOCKER_HOST_USERNAME}"
  log.info "This script should be executed from the host's command line. NOT inside the docker container"

  if ${REINIT_SSH_KEYS_ONLY}; then
    log.info "Only ssh keys will be reinited on this run"
  fi

  if ${REINIT_KAMEHOUSE_FOLDER_ONLY}; then
    log.info "Only .kamehouse folder will be reinited on this run"
  fi

  if [ "${REINIT_SSH_KEYS_ONLY}" == "false" ] &&
      [ "${REINIT_KAMEHOUSE_FOLDER_ONLY}" == "false" ]; then
    log.info "Both ssh keys and .kamehouse folder will be reinited on this run"
  fi

  if [ "${DATA_SOURCE}" == "docker-defaults" ] ||
      [ "${DATA_SOURCE}" == "docker-data" ] ||
      [ "${DATA_SOURCE}" == "host-data" ]; then
    if [ "${REINIT_SSH_KEYS_ONLY}" == "false" ] &&
        [ "${REINIT_KAMEHOUSE_FOLDER_ONLY}" == "false" ]; then
      log.warn "${COL_YELLOW}WARNING!! persisted data in the container's database will be overwritten with -d ${COL_RED}${DATA_SOURCE}"
    fi
  else
    log.info "Persisted data in the container's database will NOT be overwritten on this run"  
  fi
}

requestConfirmation() {
  echo ""
  log.info "Do you want to proceed? (${COL_BLUE}Yes${COL_DEFAULT_LOG}/${COL_RED}No${COL_DEFAULT_LOG}): "
  sleep 1
  read SHOULD_PROCEED
  SHOULD_PROCEED=`echo "${SHOULD_PROCEED}" | tr '[:upper:]' '[:lower:]'`
  if [[ "${SHOULD_PROCEED}" =~ ${REQUEST_CONFIRMATION_RX} ]]; then
    log.info "Proceeding"
  else
    log.warn "${COL_PURPLE}${SCRIPT_NAME}${COL_DEFAULT_LOG} cancelled by the user"
    exitProcess ${EXIT_PROCESS_CANCELLED}
  fi
}

runScpCommand() {
  executeScpCommand
  sleep 1
}

runSshCommand() {
  executeSshCommand
  sleep 1
}

reinitSsh() {
  log.info "${COL_RED}Reinit .ssh folder"
  syncSshKeys
  fixSshFolderPermissions
  sshFromDockerContainerToHost
}

syncSshKeys() {
  log.info "Syncing .ssh keys"
  SCP_SRC="scp://${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP}/${DOCKER_HOST_USERHOME}/.ssh/id_*"
  SCP_DEST="scp://${DOCKER_USERNAME}@localhost:${DOCKER_PORT_SSH}//home/${DOCKER_USERNAME}/.ssh/"
  runScpCommand

  SCP_SRC="scp://${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP}/${DOCKER_HOST_USERHOME}/.ssh/authorized_keys"
  SCP_DEST="scp://${DOCKER_USERNAME}@localhost:${DOCKER_PORT_SSH}//home/${DOCKER_USERNAME}/.ssh/"
  runScpCommand
}

fixSshFolderPermissions() {
  log.info "Fixing .ssh folder permissions on docker container"
  SSH_COMMAND="/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-container/docker-chmod-ssh.sh"
  runSshCommand
}

sshFromDockerContainerToHost() {
  log.info "Attempting to connect through ssh from docker container to host..."
  SSH_COMMAND="/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-container/docker-sync-ssh-keys-with-host.sh"
  runSshCommand
  log.warn "If the last command didn't display '${COL_RED}ssh from docker container to host successful${COL_DEFAULT_LOG}' then login to the container and ssh from the container to the host using \${DOCKER_HOST_IP} to add the host key to known hosts file in the container. ${COL_YELLOW}If this is not done, then the automated ssh commands won't work"
}

reinitKameHouseFolder() {
  log.info "${COL_RED}Reinit .kamehouse folder"
  if [ "${DATA_SOURCE}" == "docker-defaults" ]; then
    SSH_COMMAND="/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-container/docker-init-kamehouse-folder-to-defaults.sh"
    runSshCommand
    return
  fi
  
  log.info "Copying kamehouse.cfg to docker"
  SCP_SRC="scp://${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP}/${DOCKER_HOST_USERHOME}/.kamehouse/config/kamehouse.cfg"
  SCP_DEST="scp://${DOCKER_USERNAME}@localhost:${DOCKER_PORT_SSH}//home/${DOCKER_USERNAME}/.kamehouse/config/"
  runScpCommand

  log.info "Copying /keys to docker"
  SCP_SRC="scp://${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP}/${DOCKER_HOST_USERHOME}/.kamehouse/config/keys"
  SCP_DEST="scp://${DOCKER_USERNAME}@localhost:${DOCKER_PORT_SSH}//home/${DOCKER_USERNAME}/.kamehouse/config/"
  runScpCommand

  local REINIT_DATA_DUMP_FOLDER=false
  local DUMP_DATA_FOLDER_SRC=""
  if [ "${DATA_SOURCE}" == "docker-data" ]; then
    REINIT_DATA_DUMP_FOLDER=true
    DUMP_DATA_FOLDER_SRC="${DOCKER_HOST_USERHOME}/.kamehouse/config/docker/mariadb"
  fi

  if [ "${DATA_SOURCE}" == "host-data" ]; then
    REINIT_DATA_DUMP_FOLDER=true
    DUMP_DATA_FOLDER_SRC="${DOCKER_HOST_USERHOME}/.kamehouse/config/mariadb"
  fi  
  
  if ! ${REINIT_DATA_DUMP_FOLDER}; then
    log.info "Skipping reinit dump data folder"
    return
  fi

  log.info "Exporting mariadb data dump file from ${DUMP_DATA_FOLDER_SRC} to the container"
  SCP_SRC="scp://${DOCKER_HOST_USERNAME}@${DOCKER_HOST_IP}/${DUMP_DATA_FOLDER_SRC}"
  SCP_DEST="scp://${DOCKER_USERNAME}@localhost:${DOCKER_PORT_SSH}//home/${DOCKER_USERNAME}/.kamehouse/config/"
  runScpCommand
}

reinitMariadb() {
  case ${DATA_SOURCE} in
  "docker-defaults"|"docker-data"|"host-data")
    log.info "${COL_RED}Reinit mariadb kamehouse db"
    SSH_COMMAND="/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb-setup-kamehouse.sh -s"
    runSshCommand

    SSH_COMMAND="/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/mariadb-restore-kamehouse.sh"
    runSshCommand
    ;;
  *) 
    log.info "Skipping reinit mariadb database data for -d '${DATA_SOURCE}'"
    ;;
  esac
}

showContainerFolderStatus() {
  log.info "docker container folder status"
  SSH_COMMAND="/home/${DOCKER_USERNAME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-container/docker-kamehouse-folder-status.sh"
  runSshCommand
}

parseArguments() {
  parseDockerProfile "$@"

  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -p)
        # parsed in a previous parse options function 
        ;;
      -d)
        DATA_SOURCE="${CURRENT_OPTION_ARG}"
        ;;
      -k)
        log.info "${COL_RED}Only .kamehouse folder will be reinited"
        REINIT_KAMEHOUSE_FOLDER_ONLY=true
        ;;
      -s)
        log.info "${COL_RED}Only ssh keys will be reinited"
        REINIT_SSH_KEYS_ONLY=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  if [ "${DATA_SOURCE}" != "none" ] &&
      [ "${DATA_SOURCE}" != "docker-defaults" ] &&
      [ "${DATA_SOURCE}" != "docker-data" ] &&
      [ "${DATA_SOURCE}" != "host-data" ]; then
    log.error "Option -d [data source] has an invalid value of ${DATA_SOURCE}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi

  setIsLinuxDockerHost
  DOCKER_HOST_USERHOME=`getDockerHostUserHome`
  setEnvForDockerProfile
  SSH_PORT="${DOCKER_PORT_SSH}"
  SSH_USER="${DOCKER_USERNAME}"
  SSH_SERVER="localhost"
  IS_REMOTE_LINUX_HOST=true
}

printHelpOptions() {
  addHelpOption "-d (docker-defaults|docker-data|host-data)" "data source to reset data from"
  addHelpOption "-k" "reinit .kamehouse folder only. doesn't reinit ssh keys or persisted data in the database"
  printDockerProfileOption
  addHelpOption "-s" "reinit ssh keys only. doesn't reinit .kamehouse folder or persisted data in the database"
}

printHelpFooter() {
  echo -e ""
  echo -e "   > When executed without ${COL_CYAN_FNT}-d${COL_NORMAL}, the script resets the container folders but doesn't change the database dump folder or persisted data in the database. Use this to refresh the configuration files in the container without changing the database dump or data, for example when I change the docker host parameters in \${HOME}/.kamehouse/config/kamehouse.cfg"
  echo -e ""  
  echo -e "   > When executed with ${COL_CYAN_FNT}-k${COL_NORMAL} and with ${COL_CYAN_FNT}-d${COL_NORMAL}, the script resets the container folders including the database dump files but doesn't reinit the persisted data in the database"
  echo -e "" 
  echo -e "   > When executed without ${COL_CYAN_FNT}-k${COL_NORMAL} and with ${COL_CYAN_FNT}-d [docker-defaults|docker-data|host-data]${COL_NORMAL} the script will also reset the database"
  echo -e "     * ${COL_CYAN_FNT}docker-defaults${COL_NORMAL}: resets the database dump files and data to docker initial defaults"
  echo -e "     * ${COL_CYAN_FNT}docker-data${COL_NORMAL}: resets the database dump files and data to the host's ${COL_NORMAL_FNT}.kamehouse/config/docker/mariadb${COL_NORMAL} dump" 
  echo -e "     * ${COL_CYAN_FNT}host-data${COL_NORMAL}: resets the database dump files and data to the host's ${COL_NORMAL_FNT}.kamehouse/config/mariadb${COL_NORMAL} dump"   
  echo -e "" 
}

main "$@"
