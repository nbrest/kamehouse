#!/bin/bash

# Startup script. This script is meant to be executed as a service at boot time by root.
# It can be deployed using rc-local-deploy.sh and then it should execute at boot.
LOG_FILE=/home/pi/logs/rc-local.log

if (( $EUID != 0 )); then
  # User not root
  echo "$(date) - User not root. This script can only be executed as root" > ${LOG_FILE}
  exit 1
fi

echo "$(date) - Starting rc-local.sh" > ${LOG_FILE}

echo "$(date) - Setup tmpfs" >> ${LOG_FILE}
/home/pi/programs/kamehouse-shell/bin/pi/startup/setup-tmpfs.sh

echo "$(date) - Starting tomcat" >> ${LOG_FILE}
su - pi -c /home/pi/programs/kamehouse-shell/bin/kamehouse/tomcat-startup.sh

echo "$(date) - Backing up server" >> ${LOG_FILE}
su - pi -c /home/pi/programs/kamehouse-shell/bin/lin/backup/backup-server.sh

echo "$(date) - Disabling swap" >> ${LOG_FILE}
/home/pi/programs/kamehouse-shell/bin/pi/startup/disable-swap.sh

#echo "$(date) - Starting no-ip client" >> ${LOG_FILE}
#/usr/local/bin/noip2
# this doesn't work. so scheduled through root cron to run every 15 mins to make sure the process keeps running

echo "$(date) - Finished rc-local.sh" >> ${LOG_FILE}

chown pi:users ${LOG_FILE}
