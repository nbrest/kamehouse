#!/bin/bash
# This script runs inside the docker container, not on the host

COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_MESSAGE=${COL_GREEN}

DOCKER_CONTAINER_USERNAME=`ls /home | grep -v "nbrest"`

. /home/${DOCKER_CONTAINER_USERNAME}/.env

main() {
  log.info "mariadb status"
  sudo mariadb kameHouse -e "show full tables;"
  sudo mariadb kameHouse < /home/${DOCKER_CONTAINER_USERNAME}/sql/status-kamehouse.sql
}

log.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"
