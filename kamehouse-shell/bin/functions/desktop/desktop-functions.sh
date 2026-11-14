KAMEHOUSE_DESKTOP_PID=""
KAMEHOUSE_DESKTOP_PATH="${HOME}/programs/kamehouse-desktop"
KAMEHOUSE_DESKTOP_VENV_PATH="${KAMEHOUSE_DESKTOP_PATH}/venv"
KAMEHOUSE_DESKTOP_PYTHON=""
KAMEHOUSE_DESKTOP_DATA_PATH=${HOME}/.kamehouse/data/desktop
KAMEHOUSE_DESKTOP_BACKGROUNDS_SUCCESS_FILE=${KAMEHOUSE_DESKTOP_DATA_PATH}/backgrounds-success.list
KAMEHOUSE_DESKTOP_BACKGROUNDS_ERROR_FILE=${KAMEHOUSE_DESKTOP_DATA_PATH}/backgrounds-error.list
KAMEHOUSE_DESKTOP_BACKGROUNDS_UNPROCESSED_FILE=${KAMEHOUSE_DESKTOP_DATA_PATH}/backgrounds-unprocessed.list

setKameHouseDesktopPython() {
  if ${IS_WIN_HOST}; then
    KAMEHOUSE_DESKTOP_PYTHON="${KAMEHOUSE_DESKTOP_VENV_PATH}/Scripts/python.exe"
  else
    KAMEHOUSE_DESKTOP_PYTHON="${KAMEHOUSE_DESKTOP_VENV_PATH}/bin/python"
  fi
}

checkKameHouseDesktopInstallation() {
  setKameHouseDesktopPython
  if [ ! -x "${KAMEHOUSE_DESKTOP_PYTHON}" ]; then
    log.error "kamehouse-desktop virtual environment not found at ${COL_PURPLE}${KAMEHOUSE_DESKTOP_VENV_PATH}"
    log.error "Run ${COL_PURPLE}install-kamehouse-desktop.sh${COL_DEFAULT_LOG} to install it"
    exitProcess ${EXIT_ERROR}
  fi
}

setKameHouseDesktopPid() {
  log.info "Searching for kamehouse-desktop process"
  if ${IS_LIN_HOST}; then
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
  touch ${KAMEHOUSE_DESKTOP_BACKGROUNDS_UNPROCESSED_FILE}
}
