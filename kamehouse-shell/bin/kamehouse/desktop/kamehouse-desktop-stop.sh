#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Searching for kamehouse-desktop process"
  if ${IS_LINUX_HOST}; then
    ps aux | grep "kamehouse-desktop.py" | grep "python" | awk '{print $2}'
    local PID=`ps aux | grep "kamehouse-desktop.py" | grep "python" | awk '{print $2}'`
    log.info "PID: ${PID}"
    if [ -z "${PID}" ]; then
      log.info "kamehouse-desktop is not running"
    else
      log.info "Killing process ${COL_PURPLE}${PID}"
      kill -9 ${PID}
    fi
  else
    local POWERSHELL_COMMAND="Get-CimInstance Win32_Process -Filter \"CommandLine like '%kamehouse-desktop.py%'\" | Select-Object Name, ProcessId, CommandLine"
    local PID=`powershell.exe -c "${POWERSHELL_COMMAND}" | grep -v "powershell.exe" | grep -v "CommandLine" | grep "python.exe" | awk '{print $2}'`
    if [ -z "${PID}" ]; then
      log.info "kamehouse-desktop is not running"
    else
      log.info "Killing process ${COL_PURPLE}${PID}"
      cmd.exe "/c taskkill.exe /PID ${PID} /F"
      powershell.exe -c "Stop-Process -Id ${PID} -Force"
    fi
  fi
}

main "$@"
