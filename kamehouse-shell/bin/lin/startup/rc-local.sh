#!/bin/bash


# Startup script. This script is meant to be executed as a service at boot time by root.
# It can be deployed using rc-local-deploy.sh and then it should execute at boot.
KAMEHOUSE_USER="nbrest"
LOG_FILE=/home/${KAMEHOUSE_USER}/logs/rc-local.log

if (( $EUID != 0 )); then
  # User not root
  echo "$(date) - User not root. This script can only be executed as root" > ${LOG_FILE}
  exit 1
fi

echo "$(date) - Starting rc-local.sh" > ${LOG_FILE}
echo "$(date) - Starting tomcat" >> ${LOG_FILE}
su - ${KAMEHOUSE_USER} -c /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/kamehouse/tomcat-startup.sh
echo "$(date) - Backing up server" >> ${LOG_FILE}
su - ${KAMEHOUSE_USER} -c /home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/lin/backup/backup-server.sh
echo "$(date) - Finished rc-local.sh" >> ${LOG_FILE}
chown ${KAMEHOUSE_USER}:users ${LOG_FILE}
