#!/bin/bash

sudo mkdir -p /var/www/www-eclipse

sudo chown ${USER}:users -R /var/www/www-eclipse
cd /var/www/www-eclipse

rm kame-house
ln -s ${HOME}/workspace-eclipse/kamehouse/kamehouse-ui/src/main/webapp kame-house

rm kame-house-groot
ln -s ${HOME}/workspace-eclipse/kamehouse/kamehouse-groot/src/main/webapp/kame-house-groot kame-house-groot

rm kame-house-mobile
ln -s ${HOME}/workspace-eclipse/kamehouse/kamehouse-mobile/www/kame-house-mobile kame-house-mobile
