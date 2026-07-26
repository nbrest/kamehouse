#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  KAMEHOUSE_USER=""
  SUDOERS_FILE="/etc/sudoers.d/kamehouse"
}

mainProcessLin() {
  log.info "Started setting sudoers for kamehouse"
  updateSudoers
  log.info "Done setting sudoers for kamehouse"
}

updateSudoers() {
  log.info "Adding ${KAMEHOUSE_USER} user to adm and sudo groups"
  # adm: to be able to tail apache httpd logs
  sudo usermod -a -G adm ${KAMEHOUSE_USER}
  sudo usermod -a -G sudo ${KAMEHOUSE_USER}
  log.info "Updating sudoers file to run kamehouse"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(root) NOPASSWD: /usr/bin/kill"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(root) NOPASSWD: /usr/bin/mariadb"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(root) NOPASSWD: /usr/bin/netstat"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(root) NOPASSWD: /usr/bin/systemctl"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(root) NOPASSWD: /sbin/poweroff"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(root) NOPASSWD: /sbin/reboot"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(root) NOPASSWD: /usr/sbin/poweroff"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(root) NOPASSWD: /usr/sbin/reboot"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(root) NOPASSWD: /usr/sbin/service"
  updateSudoersEntry "${KAMEHOUSE_USER} ALL=(root) NOPASSWD: /usr/sbin/shutdown"
}

updateSudoersEntry() {
  local SUDOERS_LINE="$1"
  sudo cat "${SUDOERS_FILE}" | grep "${SUDOERS_LINE}" > /dev/null
  if [ "$?" != "0" ]; then
    log.info "${COL_RED}${SUDOERS_LINE} NOT in sudoers file. Adding it"
    sudo /bin/bash -c "echo \"\" >> ${SUDOERS_FILE}"
    sudo /bin/bash -c "echo \"${SUDOERS_LINE}\" >> ${SUDOERS_FILE}"
  else 
    log.info "'${SUDOERS_LINE}' is already in sudoers. No need to update"
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
      -u)
        KAMEHOUSE_USER="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done
}

setEnvFromArguments() {
  checkRequiredOption "-u" "${KAMEHOUSE_USER}" 
}

printHelpOptions() {
  addHelpOption "-u (username)" "user running kamehouse" "r"
}

main "$@"
