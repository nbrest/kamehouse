KAMEHOUSE_SNAPE_INSTALL_PATH="${HOME}/programs/kamehouse-snape"
KAMEHOUSE_SNAPE_PATH="${KAMEHOUSE_SNAPE_INSTALL_PATH}/bin"
KAMEHOUSE_SNAPE_VENV_PATH="${KAMEHOUSE_SNAPE_INSTALL_PATH}/venv"
KAMEHOUSE_SNAPE_PYTHON=""

setKameHouseSnapePython() {
  if ${IS_WIN_HOST}; then
    KAMEHOUSE_SNAPE_PYTHON="${KAMEHOUSE_SNAPE_VENV_PATH}/Scripts/python.exe"
  else
    KAMEHOUSE_SNAPE_PYTHON="${KAMEHOUSE_SNAPE_VENV_PATH}/bin/python"
  fi
}

checkKameHouseSnapeInstallation() {
  setKameHouseSnapePython
  if [ ! -x "${KAMEHOUSE_SNAPE_PYTHON}" ]; then
    log.error "kamehouse-snape virtual environment not found at ${COL_PURPLE}${KAMEHOUSE_SNAPE_VENV_PATH}"
    log.error "Run ${COL_PURPLE}install-kamehouse-snape.sh${COL_DEFAULT_LOG} to install it"
    exitProcess ${EXIT_ERROR}
  fi
}
