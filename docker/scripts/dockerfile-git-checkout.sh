#!/bin/bash
# This script runs while building the dockerfile

SCRIPT_NAME=`basename "$0"`
COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_PURPLE_STD="\033[0;35m"
COL_MESSAGE=${COL_GREEN}

main() {
  log.info "Checking out git branch for tag ${DOCKER_IMAGE_TAG}"
  local DOCKER_IMAGE_TAG=$1
  if [ "${DOCKER_IMAGE_TAG}" == "latest" ]; then
    git checkout dev
  else
    git checkout tags/${DOCKER_IMAGE_TAG} -b ${DOCKER_IMAGE_TAG}
  fi
  git branch -D master
}

log.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_PURPLE_STD}${SCRIPT_NAME}${COL_NORMAL} - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"
