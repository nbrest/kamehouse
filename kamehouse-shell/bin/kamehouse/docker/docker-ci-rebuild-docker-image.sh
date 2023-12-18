#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=true

mainProcess() {
  log.info "Started rebuilding kamehouse docker image"
  
  setKameHouseRootProjectDir

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-build-push-all-kamehouse.sh 
  checkCommandStatus "$?" "Error rebuilding and pushing the kamehouse docker image" 

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-upgrade-containers.sh 
  checkCommandStatus "$?" "Error upgrading local containers" 

  log.info "Finished rebuilding kamehouse docker image"
}

parseArguments() {
  while getopts ":c" OPT; do
    case $OPT in
    ("c")
      USE_CURRENT_DIR=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

printHelpOptions() {
  addHelpOption "-c" "rebuild the docker image using the current directory. Default dir: ${PROJECT_DIR}"
}

main "$@"
