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
