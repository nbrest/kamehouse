#!/bin/bash

sudo mkdir -p /var/www/www-intellij

sudo chown ${USER}:users -R /var/www/www-intellij
cd /var/www/www-intellij

rm kame-house-mobile
ln -s ${HOME}/workspace-intellij/kamehouse/kamehouse-mobile/www/kame-house-mobile kame-house-mobile
