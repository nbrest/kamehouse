#!/bin/bash

sudo mkdir -p /var/www/kamehouse-webserver

sudo chown ${USER}:users -R /var/www/kamehouse-webserver
cd /var/www/kamehouse-webserver
rm kame-house
ln -s ${HOME}/git/kamehouse/kamehouse-ui/src/main/webapp kame-house

rm kame-house-groot
ln -s ${HOME}/git/kamehouse/kamehouse-groot/public/kame-house-groot kame-house-groot
