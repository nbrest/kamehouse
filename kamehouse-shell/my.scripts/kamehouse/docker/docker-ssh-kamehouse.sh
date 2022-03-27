#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/my.scripts/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

PROFILE="dev"

mainProcess() {
  ssh -p ${DOCKER_PORT_SSH} ${DOCKER_USERNAME}@localhost
}

parseArguments() {
  while getopts ":hp:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
    ("p")
      PROFILE=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done

  if [ "${PROFILE}" != "ci" ] &&
    [ "${PROFILE}" != "dev" ] &&
    [ "${PROFILE}" != "demo" ] &&
    [ "${PROFILE}" != "prod" ] &&
    [ "${PROFILE}" != "prod-80-443" ]; then
    log.error "Option -p [profile] has an invalid value of ${DOCKER_BASE_OS}"
    printHelp
    exitProcess 1
  fi
  
  if [ "${PROFILE}" == "ci" ]; then
    DOCKER_PORT_SSH=15022
  fi

  if [ "${PROFILE}" == "demo" ]; then
    DOCKER_PORT_SSH=12022
  fi

  if [ "${PROFILE}" == "prod" ]; then
    DOCKER_PORT_SSH=7022
  fi

  if [ "${PROFILE}" == "prod-80-443" ]; then
    DOCKER_PORT_SSH=7022
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-p (ci|dev|demo|prod|prod-80-443)${COL_NORMAL} default profile is dev"
}

main "$@"
