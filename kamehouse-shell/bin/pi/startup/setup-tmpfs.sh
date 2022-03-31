#!/bin/bash

echo "$(date) - Starting setup-tmpfs.sh"
# create my custom logs dirs
mkdir -p /var/log/apache-tomcat
mkdir -p /var/log/home-pi
# create default raspberry pi logs dirs
mkdir -p /var/log/apache2
mkdir -p /var/log/apt
mkdir -p /var/log/firebird
mkdir -p /var/log/letsencrypt
mkdir -p /var/log/lightdm
mkdir -p /var/log/mysql
mkdir -p /var/log/private
mkdir -p /var/log/samba
chmod a+w -R /var/log

# create /home directories in tmpfs
mkdir -p /tmp/home/root
mkdir -p /tmp/home/pi

# setup .bash_history on tmpfs
touch /tmp/home/root/.bash_history
touch /tmp/home/pi/.bash_history
chown pi:users -R /tmp/home/pi

rm /root/.bash_history
ln -s /tmp/home/root/.bash_history /root/.bash_history

rm /home/pi/.bash_history
ln -s /tmp/home/pi/.bash_history /home/pi/.bash_history
chown pi:users /home/pi/.bash_history

echo "$(date) - Finished setup-tmpfs.sh"

