#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  echo ""
  log.info "Docker containers"
  echo ""
  docker container list

  echo ""
  log.info "Docker images"
  echo ""
  docker images

  echo ""
  log.info "Docker volumes"
  echo ""
  docker volume ls
}

main "$@"
