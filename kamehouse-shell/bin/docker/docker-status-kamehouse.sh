#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/docker/docker-functions.sh

mainProcess() {
  log.info "Checking docker status on current server ${COL_PURPLE}${HOSTNAME}"
  echo ""
  log.info "Docker containers"
  echo ""
  docker container list -a

  echo ""
  log.info "Docker images"
  echo ""
  docker images -a

  echo ""
  log.info "Docker volumes"
  echo ""
  docker volume ls
}

main "$@"
