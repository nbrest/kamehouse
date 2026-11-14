#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/docker/docker-functions.sh

mainProcess() {
  checkDockerScripsEnabled
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/docker/docker-pull-kamehouse.sh
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/docker/docker-stop-kamehouse.sh -p prod
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/docker/docker-stop-kamehouse.sh -p demo
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/docker/docker-stop-kamehouse.sh -p dev
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/docker/docker-stop-kamehouse.sh -p ci
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/docker/docker-cleanup-kamehouse.sh
  ${HOME}/.kamehouse/opt/kamehouse-shell/bin/docker/docker-status-kamehouse.sh
}

main "$@"
