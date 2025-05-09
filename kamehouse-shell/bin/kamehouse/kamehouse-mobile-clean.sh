#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

mainProcess() {
  cdToRootDirFromModule "kamehouse-mobile"
  log.info "Removing all non kamehouse-mobile files from directory ${COL_PURPLE}$(pwd)"
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-static-files.sh -c -d
}

main "$@"
