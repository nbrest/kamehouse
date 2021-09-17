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
mkdir -p /usr/share/tomcat9/logs
cp -r /usr/share/tomcat9/etc /usr/share/tomcat9/conf
/usr/share/tomcat9/bin/startup.sh

echo "" > /root/.startup.lock
tail -f /root/.startup.lock
read