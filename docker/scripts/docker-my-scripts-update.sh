#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  log.info "Updating my.scripts from java.web.kamehouse git repository"
  cp -r -v /home/nbrest/git/java.web.kamehouse/kamehouse-shell/my.scripts /home/nbrest/
  chmod a+x -R /home/nbrest/my.scripts
}

main "$@"
