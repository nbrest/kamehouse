#!/bin/bash
echo "Docker startup script"

echo "Setting path"
PATH=${PATH}:${HOME}/my.scripts

echo "Starting mysql"
service mysql start

echo "Starting apache2"
service apache2 start

echo "Starting tomcat9"
cd /root/programs/apache-tomcat 
/usr/share/tomcat9/bin/startup.sh

echo "" > /root/.startup.lock
tail -f /root/.startup.lock
read