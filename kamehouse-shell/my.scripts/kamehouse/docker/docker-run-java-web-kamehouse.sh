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

DEPLOY_KAMEHOUSE=true
PULL_KAMEHOUSE=true

mainProcess() {
  log.info "Running image nbrest/java.web.kamehouse:latest"
  log.warn "This temporary container will be removed when it exits"
  log.info "Running with DEPLOY_KAMEHOUSE=${DEPLOY_KAMEHOUSE} and PULL_KAMEHOUSE=${PULL_KAMEHOUSE}"
  docker run --rm --env DEPLOY_KAMEHOUSE=${DEPLOY_KAMEHOUSE} --env PULL_KAMEHOUSE=${PULL_KAMEHOUSE} -p 6022:22 -p 6080:80 -p 6443:443 -p 6090:9090 nbrest/java.web.kamehouse:latest
}

parseArguments() {
  while getopts ":dhp" OPT; do
    case $OPT in
    ("d")
      DEPLOY_KAMEHOUSE=false
      ;;
    ("h")
      parseHelp
      ;;
    ("p")
      PULL_KAMEHOUSE=false      
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
  
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-d${COL_NORMAL} deploy kamehouse on startup DISABLED (it's enabled by default)"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-p${COL_NORMAL} pull kamehouse on startup DISABLED (it's enabled by default)"
}

main "$@"