#!/bin/bash

# Startup script. This script is meant to be executed as a service at boot time by root.
# It can be deployed using rc-local-deploy.sh and then it should execute at boot.
LOG_FILE=/home/nbrest/logs/rc-local.log

if (( $EUID != 0 )); then
  # User not root
  echo "$(date) - User not root. This script can only be executed as root" > ${LOG_FILE}
  exit 1
fi

echo "$(date) - Starting rc-local.sh" > ${LOG_FILE}
echo "$(date) - Starting tomcat" >> ${LOG_FILE}
su - nbrest -c /home/nbrest/programs/kamehouse-shell/bin/kamehouse/tomcat-startup.sh
echo "$(date) - Backing up server" >> ${LOG_FILE}
su - nbrest -c /home/nbrest/programs/kamehouse-shell/bin/lin/backup/backup-server.sh
echo "$(date) - Finished rc-local.sh" >> ${LOG_FILE}
chown nbrest:users ${LOG_FILE}
