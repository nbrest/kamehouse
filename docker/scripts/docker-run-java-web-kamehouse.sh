#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  log.info "Running image nbrest/java.web.kamehouse:latest"
  log.warn "This temporary container will be removed when it exits"
  
  docker run --rm -p 6022:22 -p 6080:80 -p 6443:443 -p 6090:9090 nbrest/java.web.kamehouse:latest
}

main "$@"
