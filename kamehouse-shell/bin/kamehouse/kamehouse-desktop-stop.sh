#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Searching for kamehouse-desktop process"
  ps aux | grep "kamehouse-desktop.py" | grep "python" | awk '{print $2}'
  local PID=`ps aux | grep "kamehouse-desktop.py" | grep "python" | awk '{print $2}'`
  log.info "PID: ${PID}"
  if [ -z "${PID}" ]; then
    log.info "kamehouse-desktop is not running"
  else
    log.info "Killing process ${COL_PURPLE}${PID}"
    kill -9 ${PID}
  fi
}

main "$@"
