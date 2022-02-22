#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  log.warn "DEPRECATED: Use docker-run script. Use persistent data volumes but temporary containers"
  exitProcess 1
  
  log.info "Recreating docker container kamehouse-docker from image nbrest/java.web.kamehouse:latest"
  docker container rm kamehouse-docker
  docker create --name kamehouse-docker -p 6022:22 -p 6080:80 -p 6443:443 -p 6090:9090 nbrest/java.web.kamehouse
}

main "$@"