#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/deploy/build-functions.sh
importKamehouse functions/groot/groot-functions.sh

initScriptEnv() {
  KAMEHOUSE_GROOT_SOURCE=`pwd`
  FORCE_INSTALL=false
  PYTHON_BIN=${PYTHON_BIN:-}
}

mainProcessLin() {
  log.info "Installing ${COL_PURPLE}kamehouse-groot service${COL_DEFAULT_LOG} to ${COL_PURPLE}${KAMEHOUSE_GROOT_PATH}"
  log.info "Using directory ${COL_PURPLE}${KAMEHOUSE_GROOT_SOURCE}${COL_DEFAULT_LOG} as the source of the service"
  checkSourcePath
  resolvePythonBin
  checkPythonVersion
  installSources
  installVirtualEnvironment
  generateGrootBuildInfo
  logKameHouseGrootStatus
  log.info "Done installing ${COL_PURPLE}kamehouse-groot service!"
}

checkSourcePath() {
  if [ ! -d "${KAMEHOUSE_GROOT_SOURCE}/kamehouse-groot/src/main/python" ] || [ ! -d "${KAMEHOUSE_GROOT_SOURCE}/.git" ]; then
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

installSources() {
  log.info "Rebuilding kamehouse-groot install directory"
  mkdir -p "${KAMEHOUSE_GROOT_PATH}" "${KAMEHOUSE_GROOT_LOG_DIR}" "${KAMEHOUSE_GROOT_PATH}/run" "${KAMEHOUSE_GROOT_PATH}/conf"
  rm -rf "${KAMEHOUSE_GROOT_PATH}/src"
  cp -rf "${KAMEHOUSE_GROOT_SOURCE}/kamehouse-groot/src" "${KAMEHOUSE_GROOT_PATH}/"
  cp -f "${KAMEHOUSE_GROOT_SOURCE}/kamehouse-groot/pyproject.toml" "${KAMEHOUSE_GROOT_PATH}/"
  cp -f "${KAMEHOUSE_GROOT_SOURCE}/kamehouse-groot/README.md" "${KAMEHOUSE_GROOT_PATH}/"
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
  if [ ! -x "${KAMEHOUSE_GROOT_PYTHON}" ] || ! "${KAMEHOUSE_GROOT_PYTHON}" -m pip --version > /dev/null 2>&1; then
    ensurePythonVenvSupport
    log.info "Creating python virtual environment at ${COL_PURPLE}${KAMEHOUSE_GROOT_VENV_PATH}"
    rm -rf "${KAMEHOUSE_GROOT_VENV_PATH}"
    "${PYTHON_BIN}" -m venv "${KAMEHOUSE_GROOT_VENV_PATH}"
    checkCommandStatus "$?" "An error occurred creating the kamehouse-groot virtual environment"
    if ! "${KAMEHOUSE_GROOT_PYTHON}" -m pip --version > /dev/null 2>&1; then
      log.info "Bootstrapping pip in the kamehouse-groot virtual environment"
      "${KAMEHOUSE_GROOT_PYTHON}" -m ensurepip --upgrade
      checkCommandStatus "$?" "An error occurred bootstrapping pip for kamehouse-groot"
    fi
  fi

  local INSTALL_STAMP="${KAMEHOUSE_GROOT_VENV_PATH}/.kamehouse-groot-installed"
  local PYPROJECT_HASH=`sha256sum "${KAMEHOUSE_GROOT_PATH}/pyproject.toml" | cut -d' ' -f1`
  if ${FORCE_INSTALL} || [ ! -f "${INSTALL_STAMP}" ] || [ "`cat "${INSTALL_STAMP}"`" != "${PYPROJECT_HASH}" ]; then
    log.info "Installing kamehouse-groot dependencies. This might require network access"
    "${KAMEHOUSE_GROOT_PYTHON}" -m pip install --upgrade pip
    checkCommandStatus "$?" "An error occurred upgrading pip for kamehouse-groot"
    "${KAMEHOUSE_GROOT_PYTHON}" -m pip install -e "${KAMEHOUSE_GROOT_PATH}"
    checkCommandStatus "$?" "An error occurred installing kamehouse-groot dependencies"
    echo "${PYPROJECT_HASH}" > "${INSTALL_STAMP}"
  else
    log.info "kamehouse-groot dependencies already installed. Skipping. Use -f to force reinstall"
  fi
}

generateGrootBuildInfo() {
  local KAMEHOUSE_GROOT_BUILD_VERSION=`getKameHouseBuildVersion`
  local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
  echo '{ "buildVersion": "'${KAMEHOUSE_GROOT_BUILD_VERSION}'", "buildDate": "'${BUILD_DATE}'" }' > "${KAMEHOUSE_GROOT_PATH}/conf/build-info.json"
}

logKameHouseGrootStatus() {
  log.info "Deployed kamehouse-groot status"
  log.info "ls -lh ${COL_CYAN_STD}${KAMEHOUSE_GROOT_PATH}"
  ls -lh "${KAMEHOUSE_GROOT_PATH}"
  log.info "${COL_YELLOW_STD}kamehouse-groot version:"
  echo -ne "${COL_YELLOW_STD}     "
  cat "${KAMEHOUSE_GROOT_PATH}/conf/build-info.json"
  echo -ne "${COL_NORMAL}"
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    case "${CURRENT_OPTION}" in
      -f)
        FORCE_INSTALL=true
        ;;
      -p)
        KAMEHOUSE_GROOT_SOURCE=${PROJECT_DIR}
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
