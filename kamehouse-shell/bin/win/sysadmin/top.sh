#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99
fi

mainProcess() {
  log.info "Executing 'powershell.exe -c \"ps | sort -desc cpu | select -first 20\"'"
  powershell.exe -c "ps | sort -desc cpu | select -first 20"
}

main "$@"
