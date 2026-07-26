#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcessPre() {
  fixPermissions
}

fixPermissions() {
  log.info "Fixing permissions on scripts in `pwd`"
  find . -regex ".*sh" -type f -exec chmod a+x {} \;
}

main "$@"
