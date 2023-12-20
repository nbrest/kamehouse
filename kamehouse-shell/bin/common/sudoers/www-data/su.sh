#!/bin/bash
# USERNAME gets set during install kamehouse-shell
USERNAME="${DEFAULT_KAMEHOUSE_USERNAME}"
COMMAND="/home/${USERNAME}/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh $@"

mkdir -p ${HOME}/logs
echo "command to execute as ${USERNAME}: ${COMMAND}" > ${HOME}/logs/su.sh

/usr/bin/su - ${USERNAME} -c "${COMMAND}"
