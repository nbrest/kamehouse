#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/deploy/build-functions.sh
importKamehouse functions/desktop/desktop-functions.sh

initScriptEnv() {
  TEMP_PATH=${HOME}/temp
  KAMEHOUSE_DESKTOP_SOURCE=`pwd`
  FORCE_INSTALL=false
  PYTHON_BIN=${PYTHON_BIN:-}
}

mainProcess() {
  log.info "Installing ${COL_PURPLE}kamehouse-desktop${COL_DEFAULT_LOG} to ${COL_PURPLE}${KAMEHOUSE_DESKTOP_PATH}"
  log.info "Using directory ${COL_PURPLE}${KAMEHOUSE_DESKTOP_SOURCE}${COL_DEFAULT_LOG} as the source of the scripts"
  checkSourcePath
  resolvePythonBin
  checkPythonVersion
  installKameHouseDesktop
  fixPermissions
  generateBuildInfo
  deploySourcesFromUiModule
  installKamehouseDesktopConfig
  installVirtualEnvironment
  logKameHouseDesktopStatus
  log.info "Done installing ${COL_PURPLE}kamehouse-desktop!"
}

checkSourcePath() {
  if [ ! -d "${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-shell/bin" ] || [ ! -d "${KAMEHOUSE_DESKTOP_SOURCE}/.git" ]; then
    log.error "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exit ${EXIT_ERROR}
  fi
}

resolvePythonBin() {
  if [ -n "${PYTHON_BIN}" ]; then
    if command -v "${PYTHON_BIN}" > /dev/null 2>&1; then
      return
    fi
    log.error "Python binary ${COL_PURPLE}${PYTHON_BIN}${COL_DEFAULT_LOG} not found"
    exitProcess ${EXIT_ERROR}
  fi
  local CANDIDATE
  for CANDIDATE in python3 python; do
    if command -v "${CANDIDATE}" > /dev/null 2>&1; then
      PYTHON_BIN="${CANDIDATE}"
      log.info "Using python binary ${COL_PURPLE}${PYTHON_BIN}${COL_DEFAULT_LOG}"
      return
    fi
  done
  log.error "Python 3.11 or newer is required but neither ${COL_PURPLE}python3${COL_DEFAULT_LOG} nor ${COL_PURPLE}python${COL_DEFAULT_LOG} was found"
  exitProcess ${EXIT_ERROR}
}

checkPythonVersion() {
  if ! "${PYTHON_BIN}" -c 'import sys; raise SystemExit(0 if sys.version_info >= (3, 11) else 1)' > /dev/null 2>&1; then
    log.error "Python 3.11 or newer is required. Found ${COL_PURPLE}$("${PYTHON_BIN}" --version 2>&1)${COL_DEFAULT_LOG}"
    exitProcess ${EXIT_ERROR}
  fi
  log.info "Using Python version ${COL_PURPLE}$("${PYTHON_BIN}" --version 2>&1)${COL_DEFAULT_LOG}"
}

installKameHouseDesktop() {
  log.info "Rebuilding desktop install directory"
  rm -r -f ${KAMEHOUSE_DESKTOP_PATH}/bin
  rm -f ${KAMEHOUSE_DESKTOP_PATH}/conf/build-info.json
  mkdir -p ${KAMEHOUSE_DESKTOP_PATH}/conf
  cp -r -f ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-desktop/bin ${KAMEHOUSE_DESKTOP_PATH}/
  cp -r -f ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-desktop/conf ${KAMEHOUSE_DESKTOP_PATH}/
  cp -r -f ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-desktop/lib ${KAMEHOUSE_DESKTOP_PATH}/
  cp -f ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-desktop/pyproject.toml ${KAMEHOUSE_DESKTOP_PATH}/
  cp -f ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-desktop/README.md ${KAMEHOUSE_DESKTOP_PATH}/
  mkdir -p ${HOME}/.kamehouse/data/desktop/backgrounds
}

fixPermissions() {
  log.info "Fixing permissions"
  local KAMEHOUSE_DESKTOP_BIN_PATH=${KAMEHOUSE_DESKTOP_PATH}/bin
  chmod -R 700 ${KAMEHOUSE_DESKTOP_BIN_PATH} 
  
  local NON_SCRIPTS=`find ${KAMEHOUSE_DESKTOP_BIN_PATH} -name '.*' -prune -o -type f | grep -v -e "\.sh$\|\.py$"`;
  while read NON_SCRIPT; do
    if [ -n "${NON_SCRIPT}" ]; then
      chmod a-x ${NON_SCRIPT}
    fi
  done <<< ${NON_SCRIPTS}

  local SCRIPTS=`find ${KAMEHOUSE_DESKTOP_BIN_PATH} -name '.*' -prune -o -type f | grep -e "\.sh$\|\.py$"`;
  while read SCRIPT; do
    if [ -n "${SCRIPT}" ]; then
      chmod u+rx ${SCRIPT}
    fi
  done <<< ${SCRIPTS}

  local FUNCTIONS=`find ${KAMEHOUSE_DESKTOP_BIN_PATH} -name '.*' -prune -o -type f | grep "\-functions.py$"`
  while read FUNCTION; do
    if [ -n "${FUNCTION}" ]; then
      chmod a-x ${FUNCTION}
    fi
  done <<< ${FUNCTIONS}

  local DIRECTORIES=`find ${KAMEHOUSE_DESKTOP_BIN_PATH} -name '.*' -prune -o -type d`
  while read DIRECTORY; do
    if [ -n "${DIRECTORY}" ]; then
      chmod u+rx ${DIRECTORY}
    fi
  done <<< ${DIRECTORIES}
}

generateBuildInfo() {
  local KAMEHOUSE_DESKTOP_CONF_PATH=${KAMEHOUSE_DESKTOP_PATH}/conf
  local KAMEHOUSE_BUILD_VERSION=`getKameHouseBuildVersion`
  local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
  echo '{ "buildVersion": "'${KAMEHOUSE_BUILD_VERSION}'", "buildDate": "'${BUILD_DATE}'" }' > ${KAMEHOUSE_DESKTOP_CONF_PATH}/build-info.json
}

deploySourcesFromUiModule() {
  log.info "Deploying source files needed from ui module for desktop"
  rm -rf "${KAMEHOUSE_DESKTOP_PATH}/lib/ui"
  mkdir -p "${KAMEHOUSE_DESKTOP_PATH}/lib/ui"
  cp -rf ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-ui/src/main/public/img ${KAMEHOUSE_DESKTOP_PATH}/lib/ui/img
}

installKamehouseDesktopConfig() {
  log.info "Installing kamehouse-desktop.toml"
  if [ ! -f "${HOME}/.kamehouse/config/kamehouse-desktop.toml" ]; then
    log.info "${COL_PURPLE}${HOME}/.kamehouse/config/kamehouse-desktop.toml${COL_MESSAGE} not found. Creating it from template"
    mkdir -p ${HOME}/.kamehouse/config/
    cp docker/setup-kamehouse/config/kamehouse-desktop.toml ${HOME}/.kamehouse/config/kamehouse-desktop.toml
  else
    log.info "kamehouse-desktop.toml file exists. skipping"
  fi
}

ensurePythonVenvSupport() {
  if "${PYTHON_BIN}" -m venv --help > /dev/null 2>&1 && "${PYTHON_BIN}" -m ensurepip --version > /dev/null 2>&1; then
    return
  fi
  log.warn "Python venv/ensurepip modules are not available for ${COL_PURPLE}${PYTHON_BIN}${COL_DEFAULT_LOG}. Installing the python venv package"
  installPythonVenvPackage
  if ! "${PYTHON_BIN}" -m venv --help > /dev/null 2>&1 || ! "${PYTHON_BIN}" -m ensurepip --version > /dev/null 2>&1; then
    log.error "Could not enable the python venv/ensurepip modules for ${COL_PURPLE}${PYTHON_BIN}${COL_DEFAULT_LOG}"
    exitProcess ${EXIT_ERROR}
  fi
}

installPythonVenvPackage() {
  if ! ${IS_LIN_HOST}; then
    log.error "Could not install the python venv package on this host. Install a Python 3.11 or newer distribution with the venv and ensurepip modules and run this script again"
    exitProcess ${EXIT_ERROR}
  fi
  if command -v apt-get > /dev/null 2>&1; then
    setSudoKameHouseCommand "apt-get update"
    ${SUDO_KAMEHOUSE_COMMAND}
    checkCommandStatus "$?" "An error occurred updating the apt package lists"
    setSudoKameHouseCommand "apt-get install -y python3-venv"
    ${SUDO_KAMEHOUSE_COMMAND}
    checkCommandStatus "$?" "An error occurred installing python3-venv"
    return
  fi
  if command -v dnf > /dev/null 2>&1; then
    setSudoKameHouseCommand "dnf install -y python3-pip"
    ${SUDO_KAMEHOUSE_COMMAND}
    checkCommandStatus "$?" "An error occurred installing python3-pip"
    return
  fi
  if command -v yum > /dev/null 2>&1; then
    setSudoKameHouseCommand "yum install -y python3-pip"
    ${SUDO_KAMEHOUSE_COMMAND}
    checkCommandStatus "$?" "An error occurred installing python3-pip"
    return
  fi
  if command -v apk > /dev/null 2>&1; then
    setSudoKameHouseCommand "apk add --no-cache py3-pip"
    ${SUDO_KAMEHOUSE_COMMAND}
    checkCommandStatus "$?" "An error occurred installing py3-pip"
    return
  fi
  log.error "Could not install the python venv package. Install python3-venv (Debian/Ubuntu), python3-pip (RHEL/Fedora) or py3-pip (Alpine) and run this script again"
  exitProcess ${EXIT_ERROR}
}

installVirtualEnvironment() {
  setKameHouseDesktopPython
  if [ ! -x "${KAMEHOUSE_DESKTOP_PYTHON}" ] || ! "${KAMEHOUSE_DESKTOP_PYTHON}" -m pip --version > /dev/null 2>&1; then
    ensurePythonVenvSupport
    log.info "Creating python virtual environment at ${COL_PURPLE}${KAMEHOUSE_DESKTOP_VENV_PATH}"
    rm -rf "${KAMEHOUSE_DESKTOP_VENV_PATH}"
    "${PYTHON_BIN}" -m venv "${KAMEHOUSE_DESKTOP_VENV_PATH}"
    checkCommandStatus "$?" "An error occurred creating the kamehouse-desktop virtual environment"
    if ! "${KAMEHOUSE_DESKTOP_PYTHON}" -m pip --version > /dev/null 2>&1; then
      log.info "Bootstrapping pip in the kamehouse-desktop virtual environment"
      "${KAMEHOUSE_DESKTOP_PYTHON}" -m ensurepip --upgrade
      checkCommandStatus "$?" "An error occurred bootstrapping pip for kamehouse-desktop"
    fi
  fi

  local INSTALL_STAMP="${KAMEHOUSE_DESKTOP_VENV_PATH}/.kamehouse-desktop-installed"
  local PYPROJECT_HASH=`sha256sum "${KAMEHOUSE_DESKTOP_PATH}/pyproject.toml" | cut -d' ' -f1`
  if ${FORCE_INSTALL} || [ ! -f "${INSTALL_STAMP}" ] || [ "`cat "${INSTALL_STAMP}"`" != "${PYPROJECT_HASH}" ]; then
    log.info "Installing kamehouse-desktop dependencies. This might require network access"
    "${KAMEHOUSE_DESKTOP_PYTHON}" -m pip install --upgrade pip
    checkCommandStatus "$?" "An error occurred upgrading pip for kamehouse-desktop"
    "${KAMEHOUSE_DESKTOP_PYTHON}" -m pip install -e "${KAMEHOUSE_DESKTOP_PATH}"
    checkCommandStatus "$?" "An error occurred installing kamehouse-desktop dependencies"
    echo "${PYPROJECT_HASH}" > "${INSTALL_STAMP}"
  else
    log.info "kamehouse-desktop dependencies already installed. Skipping. Use -f to force reinstall"
  fi
}

logKameHouseDesktopStatus() {
  log.info "Deployed kamehouse-desktop status"
  log.info "ls -lh ${COL_CYAN_STD}${KAMEHOUSE_DESKTOP_PATH}"
  ls -lh "${KAMEHOUSE_DESKTOP_PATH}"
  log.info "${COL_YELLOW_STD}kamehouse-desktop version:"
  echo -ne "${COL_YELLOW_STD}     "
  cat "${KAMEHOUSE_DESKTOP_PATH}/conf/build-info.json"
  echo -ne "${COL_NORMAL}"
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -f)
        FORCE_INSTALL=true
        ;;
      -p)
        KAMEHOUSE_DESKTOP_SOURCE=${HOME}/git/kamehouse
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done
}

printHelpOptions() {
  addHelpOption "-f" "force reinstall of the python virtual environment dependencies"
  addHelpOption "-p" "use kamehouse git prod directory instead of current dir"
}

main "$@"
