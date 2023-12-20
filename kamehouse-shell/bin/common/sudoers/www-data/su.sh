#!/bin/bash
source /var/www/.kamehouse-user

COMMAND="/home/${KAMEHOUSE_USER}/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh $@"

/usr/bin/su - ${KAMEHOUSE_USER} -c "${COMMAND}"
