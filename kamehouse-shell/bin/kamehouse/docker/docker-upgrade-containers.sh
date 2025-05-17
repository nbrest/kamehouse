#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing docker-functions.sh" ; exit 99 ; fi

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
