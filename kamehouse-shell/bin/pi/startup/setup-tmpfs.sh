#!/bin/bash

# as user pi run once the following commands:
#    ln -s /tmp/home/pi/programs/apache-tomcat/temp /home/pi/programs/apache-tomcat/temp
#    ln -s /var/log/home/pi/programs/apache-tomcat/logs /home/pi/programs/apache-tomcat/logs
#    ln -s /var/log/home/pi/logs /home/pi/logs
# as user root run once the following commands:
#    ln -s /var/log/home/root/logs /root/logs

main() {
  log.info "Starting setup-tmpfs.sh"
  createTmpDirs
  createHomeLogDirs
  createDefaultPiLogDirs
  createBashHistoryFiles
  setPermissions
  log.info "Finished setup-tmpfs.sh"
}

createTmpDirs() {
  log.info "create /home directories in tmpfs"
  mkdir -p /tmp/home/root
  mkdir -p /tmp/home/pi
  mkdir -p /tmp/home/pi/programs/apache-tomcat/temp
}

createHomeLogDirs() {
  log.info "create home logs directories"
  mkdir -p /var/log/home/root
  mkdir -p /var/log/home/pi
  mkdir -p /var/log/home/pi/programs/apache-tomcat/logs
  mkdir -p /var/log/home/root/logs
  mkdir -p /var/log/home/pi/logs
}

createDefaultPiLogDirs() {
  log.info "create default raspberry pi logs directories"
  mkdir -p /var/log/apache2
  mkdir -p /var/log/apt
  mkdir -p /var/log/firebird
  mkdir -p /var/log/letsencrypt
  mkdir -p /var/log/lightdm
  mkdir -p /var/log/mysql
  mkdir -p /var/log/private
  mkdir -p /var/log/samba
}

createBashHistoryFiles() {
  log.info "setup .bash_history on tmpfs"
  touch /tmp/home/root/.bash_history
  rm /root/.bash_history
  ln -s /tmp/home/root/.bash_history /root/.bash_history

  touch /tmp/home/pi/.bash_history
  rm /home/pi/.bash_history
  ln -s /tmp/home/pi/.bash_history /home/pi/.bash_history
  chown pi:users /home/pi/.bash_history
}

setPermissions() {
  log.info "Set permissions on /var/log and /tmp"
  chmod -R a+w /var/log
  chmod -R a+w /tmp/

  chown pi:users -R /var/log/home/pi
  chown pi:users -R /tmp/home/pi

  touch /var/log/btmp
  chmod 0600 /var/log/btmp
}

log.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"