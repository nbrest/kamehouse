KAMEHOUSE_DESKTOP_PID=""
KAMEHOUSE_DESKTOP_DATA_PATH=${HOME}/.kamehouse/data/desktop
KAMEHOUSE_DESKTOP_BACKGROUNDS_SUCCESS_FILE=${KAMEHOUSE_DESKTOP_DATA_PATH}/backgrounds-success.list
KAMEHOUSE_DESKTOP_BACKGROUNDS_ERROR_FILE=${KAMEHOUSE_DESKTOP_DATA_PATH}/backgrounds-error.list

setKameHouseDesktopPid() {
  log.info "Searching for kamehouse-desktop process"
  if ${IS_LINUX_HOST}; then
    ps aux | grep "kamehouse_desktop.py" | grep "python" | awk '{print $2}'
    KAMEHOUSE_DESKTOP_PID=`ps aux | grep "kamehouse_desktop.py" | grep "python" | awk '{print $2}'`
  else
    local POWERSHELL_COMMAND="Get-CimInstance Win32_Process -Filter \"CommandLine like '%kamehouse_desktop.py%'\" | Select-Object Name, ProcessId, CommandLine"
    KAMEHOUSE_DESKTOP_PID=`powershell.exe -c "${POWERSHELL_COMMAND}" | grep -v "powershell.exe" | grep -v "CommandLine" | grep "python.exe" | awk '{print $2}' | tail -n 1`
  fi
  log.info "KAMEHOUSE_DESKTOP_PID: ${KAMEHOUSE_DESKTOP_PID}"
}

initDesktopBackgroundsLists() {
  mkdir -p ${KAMEHOUSE_DESKTOP_DATA_PATH}
  touch ${KAMEHOUSE_DESKTOP_BACKGROUNDS_SUCCESS_FILE}
  touch ${KAMEHOUSE_DESKTOP_BACKGROUNDS_ERROR_FILE}
}
