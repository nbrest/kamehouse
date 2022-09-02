#!/bin/bash

# as user pi run once the following commands:
#    ln -s /tmp/home/pi/programs/apache-tomcat/temp /home/pi/programs/apache-tomcat/temp
#    ln -s /var/log/home/pi/programs/apache-tomcat/logs /home/pi/programs/apache-tomcat/logs
#    ln -s /var/log/home/pi/logs /home/pi/logs
# as user root run once the following commands:
#    ln -s /var/log/home/root/logs /root/logs

main() {
  echo "$(date) - Starting setup-tmpfs.sh"
  createTmpDirs
  createHomeLogDirs
  createDefaultPiLogDirs
  createBashHistoryFiles
  setPermissions
  echo "$(date) - Finished setup-tmpfs.sh"
}

createTmpDirs() {
  # create /home directories in tmpfs
  mkdir -p /tmp/home/root
  mkdir -p /tmp/home/pi
  mkdir -p /tmp/home/pi/programs/apache-tomcat/temp
}

createHomeLogDirs() {
  mkdir -p /var/log/home/root
  mkdir -p /var/log/home/pi
  mkdir -p /var/log/home/pi/programs/apache-tomcat/logs
  mkdir -p /var/log/home/root/logs
  mkdir -p /var/log/home/pi/logs
}

createDefaultPiLogDirs() {
  # create default raspberry pi logs dirs
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
  # setup .bash_history on tmpfs
  touch /tmp/home/root/.bash_history
  rm /root/.bash_history
  ln -s /tmp/home/root/.bash_history /root/.bash_history

  touch /tmp/home/pi/.bash_history
  rm /home/pi/.bash_history
  ln -s /tmp/home/pi/.bash_history /home/pi/.bash_history
  chown pi:users /home/pi/.bash_history
}

setPermissions() {
  # Set permissions on /var/log and /tmp
  chmod -R a+w /var/log
  chmod -R a+w /tmp/

  chown pi:users -R /var/log/home/pi
  chown pi:users -R /tmp/home/pi
}

main "$@"