KAMEHOUSE_DESKTOP_PID=""

setKameHouseDesktopPid() {
  log.info "Searching for kamehouse-desktop process"
  if ${IS_LINUX_HOST}; then
    ps aux | grep "kamehouse_desktop.py" | grep "python" | awk '{print $2}'
    KAMEHOUSE_DESKTOP_PID=`ps aux | grep "kamehouse_desktop.py" | grep "python" | awk '{print $2}'`
  else
    local POWERSHELL_COMMAND="Get-CimInstance Win32_Process -Filter \"CommandLine like '%kamehouse_desktop.py%'\" | Select-Object Name, ProcessId, CommandLine"
    KAMEHOUSE_DESKTOP_PID=`powershell.exe -c "${POWERSHELL_COMMAND}" | grep -v "powershell.exe" | grep -v "CommandLine" | grep "python.exe" | awk '{print $2}'`
  fi
  log.info "KAMEHOUSE_DESKTOP_PID: ${KAMEHOUSE_DESKTOP_PID}"
}