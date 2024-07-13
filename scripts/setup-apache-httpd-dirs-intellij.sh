#!/bin/bash

sudo mkdir -p /var/www/www-intellij

sudo chown ${USER}:users -R /var/www/www-intellij
cd /var/www/www-intellij

rm kame-house
ln -s ${HOME}/workspace-intellij/kamehouse/kamehouse-ui/dist kame-house

rm kame-house-groot
ln -s ${HOME}/workspace-intellij/kamehouse/kamehouse-groot/src/main/webapp/kame-house-groot kame-house-groot

rm kame-house-mobile
ln -s ${HOME}/workspace-intellij/kamehouse/kamehouse-mobile/www/kame-house-mobile kame-house-mobile
