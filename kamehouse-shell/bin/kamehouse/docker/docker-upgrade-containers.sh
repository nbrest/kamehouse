#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 99
fi

LOG_PROCESS_TO_FILE=true

mainProcess() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-pull-kamehouse.sh
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-stop-kamehouse.sh -p prod
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-stop-kamehouse.sh -p demo
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-stop-kamehouse.sh -p dev
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-stop-kamehouse.sh -p ci
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-cleanup-kamehouse.sh
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-status-kamehouse.sh
}

main "$@"
