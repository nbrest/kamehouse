#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  chmod 755 ${HOME}
  chmod 700 ${HOME}/.ssh 
  chmod 644 ${HOME}/.ssh/authorized_keys
  chmod 600 ${HOME}/.ssh/config
  chmod 600 ${HOME}/.ssh/id_* 
  chmod 644 ${HOME}/.ssh/*.pub
  chmod 644 ${HOME}/.ssh/known_hosts
}

main "$@"
