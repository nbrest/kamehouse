#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/deploy/build-functions.sh
importKamehouse functions/snape/snape-functions.sh

initScriptEnv() {
  KAMEHOUSE_SNAPE_SOURCE=`pwd`
  FORCE_INSTALL=false
  PYTHON_BIN=${PYTHON_BIN:-}
}

mainProcess() {
  log.info "Installing ${COL_PURPLE}kamehouse-snape${COL_DEFAULT_LOG} to ${COL_PURPLE}${KAMEHOUSE_SNAPE_INSTALL_PATH}"
  log.info "Using directory ${COL_PURPLE}${KAMEHOUSE_SNAPE_SOURCE}${COL_DEFAULT_LOG} as the source of the scripts"
  checkSourcePath
  resolvePythonBin
  checkPythonVersion
  installKameHouseSnape
  fixPermissions
  generateBuildInfo
  installKamehouseSnapeConfig
  installVirtualEnvironment
  logKameHouseSnapeStatus
  log.info "Done installing ${COL_PURPLE}kamehouse-snape!"
}

checkSourcePath() {
  if [ ! -d "${KAMEHOUSE_SNAPE_SOURCE}/kamehouse-shell/bin" ] || [ ! -d "${KAMEHOUSE_SNAPE_SOURCE}/.git" ]; then
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

installKameHouseSnape() {
  log.info "Rebuilding snape scripts directory"
  rm -r -f ${KAMEHOUSE_SNAPE_PATH}
  rm -f ${KAMEHOUSE_SNAPE_INSTALL_PATH}/conf/build-info.json
  mkdir -p ${KAMEHOUSE_SNAPE_INSTALL_PATH}/conf
  cp -r -f ${KAMEHOUSE_SNAPE_SOURCE}/kamehouse-snape/bin ${KAMEHOUSE_SNAPE_INSTALL_PATH}/
  cp -r -f ${KAMEHOUSE_SNAPE_SOURCE}/kamehouse-snape/conf ${KAMEHOUSE_SNAPE_INSTALL_PATH}/
  cp -f ${KAMEHOUSE_SNAPE_SOURCE}/kamehouse-snape/pyproject.toml ${KAMEHOUSE_SNAPE_INSTALL_PATH}/
  cp -f ${KAMEHOUSE_SNAPE_SOURCE}/kamehouse-snape/README.md ${KAMEHOUSE_SNAPE_INSTALL_PATH}/
  chmod -R 755 ${KAMEHOUSE_SNAPE_PATH} ${KAMEHOUSE_SNAPE_INSTALL_PATH}/conf
}

fixPermissions() {
  log.info "Fixing scripts permissions"
  local KAMEHOUSE_SNAPE_BIN_PATH=${KAMEHOUSE_SNAPE_PATH}
  chmod -R 755 ${KAMEHOUSE_SNAPE_BIN_PATH} 
  
  local NON_SCRIPTS=`find ${KAMEHOUSE_SNAPE_BIN_PATH} -name '.*' -prune -o -type f | grep -v -e "\.sh$\|\.py$"`;
  while read NON_SCRIPT; do
    if [ -n "${NON_SCRIPT}" ]; then
      chmod a-x ${NON_SCRIPT}
    fi
  done <<< ${NON_SCRIPTS}

  local SCRIPTS=`find ${KAMEHOUSE_SNAPE_BIN_PATH} -name '.*' -prune -o -type f | grep -e "\.sh$\|\.py$"`;
  while read SCRIPT; do
    if [ -n "${SCRIPT}" ]; then
      chmod a+rx ${SCRIPT}
    fi
  done <<< ${SCRIPTS}

  local FUNCTIONS=`find ${KAMEHOUSE_SNAPE_BIN_PATH} -name '.*' -prune -o -type f | grep "\-functions.py$"`
  while read FUNCTION; do
    if [ -n "${FUNCTION}" ]; then
      chmod a-x ${FUNCTION}
    fi
  done <<< ${FUNCTIONS}

  local DIRECTORIES=`find ${KAMEHOUSE_SNAPE_BIN_PATH} -name '.*' -prune -o -type d`
  while read DIRECTORY; do
    if [ -n "${DIRECTORY}" ]; then
      chmod a+rx ${DIRECTORY}
    fi
  done <<< ${DIRECTORIES}
}

generateBuildInfo() {
  local KAMEHOUSE_SNAPE_CONF_PATH=${KAMEHOUSE_SNAPE_INSTALL_PATH}/conf
  local KAMEHOUSE_BUILD_VERSION=`getKameHouseBuildVersion`
  local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
  echo '{ "buildVersion": "'${KAMEHOUSE_BUILD_VERSION}'", "buildDate": "'${BUILD_DATE}'" }' > ${KAMEHOUSE_SNAPE_CONF_PATH}/build-info.json
}

installKamehouseSnapeConfig() {
  log.info "Installing kamehouse-snape.toml"
  if [ ! -f "${HOME}/.kamehouse/config/kamehouse-snape.toml" ]; then
    log.info "${COL_PURPLE}${HOME}/.kamehouse/config/kamehouse-snape.toml${COL_MESSAGE} not found. Creating it from template"
    mkdir -p ${HOME}/.kamehouse/config/
    cp docker/setup-kamehouse/config/kamehouse-snape.toml ${HOME}/.kamehouse/config/kamehouse-snape.toml
  else
    log.info "kamehouse-snape.toml file exists. skipping"
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
  setKameHouseSnapePython
  if [ ! -x "${KAMEHOUSE_SNAPE_PYTHON}" ] || ! "${KAMEHOUSE_SNAPE_PYTHON}" -m pip --version > /dev/null 2>&1; then
    ensurePythonVenvSupport
    log.info "Creating python virtual environment at ${COL_PURPLE}${KAMEHOUSE_SNAPE_VENV_PATH}"
    rm -rf "${KAMEHOUSE_SNAPE_VENV_PATH}"
    "${PYTHON_BIN}" -m venv "${KAMEHOUSE_SNAPE_VENV_PATH}"
    checkCommandStatus "$?" "An error occurred creating the kamehouse-snape virtual environment"
    if ! "${KAMEHOUSE_SNAPE_PYTHON}" -m pip --version > /dev/null 2>&1; then
      log.info "Bootstrapping pip in the kamehouse-snape virtual environment"
      "${KAMEHOUSE_SNAPE_PYTHON}" -m ensurepip --upgrade
      checkCommandStatus "$?" "An error occurred bootstrapping pip for kamehouse-snape"
    fi
  fi

  local INSTALL_STAMP="${KAMEHOUSE_SNAPE_VENV_PATH}/.kamehouse-snape-installed"
  local PYPROJECT_HASH=`sha256sum "${KAMEHOUSE_SNAPE_INSTALL_PATH}/pyproject.toml" | cut -d' ' -f1`
  if ${FORCE_INSTALL} || [ ! -f "${INSTALL_STAMP}" ] || [ "`cat "${INSTALL_STAMP}"`" != "${PYPROJECT_HASH}" ]; then
    log.info "Installing kamehouse-snape dependencies. This might require network access"
    "${KAMEHOUSE_SNAPE_PYTHON}" -m pip install --upgrade pip
    checkCommandStatus "$?" "An error occurred upgrading pip for kamehouse-snape"
    "${KAMEHOUSE_SNAPE_PYTHON}" -m pip install -e "${KAMEHOUSE_SNAPE_INSTALL_PATH}"
    checkCommandStatus "$?" "An error occurred installing kamehouse-snape dependencies"
    echo "${PYPROJECT_HASH}" > "${INSTALL_STAMP}"
  else
    log.info "kamehouse-snape dependencies already installed. Skipping. Use -f to force reinstall"
  fi
}

logKameHouseSnapeStatus() {
  log.info "Deployed kamehouse-snape status"
  log.info "ls -lh ${COL_CYAN_STD}${KAMEHOUSE_SNAPE_INSTALL_PATH}"
  ls -lh "${KAMEHOUSE_SNAPE_INSTALL_PATH}"
  log.info "${COL_YELLOW_STD}kamehouse-snape version:"
  echo -ne "${COL_YELLOW_STD}     "
  cat "${KAMEHOUSE_SNAPE_INSTALL_PATH}/conf/build-info.json"
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
        KAMEHOUSE_SNAPE_SOURCE=${HOME}/git/kamehouse
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
