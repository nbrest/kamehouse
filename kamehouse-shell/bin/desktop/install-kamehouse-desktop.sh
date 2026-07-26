#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/deploy/build-functions.sh

initScriptEnv() {
  KAMEHOUSE_DESKTOP_PATH=${HOME}/programs/kamehouse-desktop
  TEMP_PATH=${HOME}/temp
  KAMEHOUSE_DESKTOP_SOURCE=`pwd`
}

mainProcessPre() {
  log.info "Installing ${COL_PURPLE}kamehouse-desktop${COL_MESSAGE} to ${COL_PURPLE}${KAMEHOUSE_DESKTOP_PATH}"
  log.info "Using directory ${COL_PURPLE}${KAMEHOUSE_DESKTOP_SOURCE}${COL_MESSAGE} as the source of the scripts"
  checkSourcePath
  installKameHouseDesktop
  fixPermissions
  generateBuildInfo
  deploySourcesFromUiModule
  installKamehouseDesktopConfig
  logKameHouseDesktopStatus
  log.info "Done installing ${COL_PURPLE}kamehouse-desktop!"
}

checkSourcePath() {
  if [ ! -d "${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-shell/bin" ] || [ ! -d "${KAMEHOUSE_DESKTOP_SOURCE}/.git" ]; then
    log.error "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exit ${EXIT_ERROR}
  fi
}

installKameHouseDesktop() {
  log.info "Rebuilding desktop install directory"
  rm -r -f ${KAMEHOUSE_DESKTOP_PATH}/bin
  rm -f ${KAMEHOUSE_DESKTOP_PATH}/conf/build-info.json
  mkdir -p ${KAMEHOUSE_DESKTOP_PATH}/conf
  cp -r -f ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-desktop/bin ${KAMEHOUSE_DESKTOP_PATH}/
  cp -r -f ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-desktop/conf ${KAMEHOUSE_DESKTOP_PATH}/
  cp -r -f ${KAMEHOUSE_DESKTOP_SOURCE}/kamehouse-desktop/lib ${KAMEHOUSE_DESKTOP_PATH}/
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
  log.info "Installing kamehouse-desktop.cfg"
  if [ ! -f "${HOME}/.kamehouse/config/kamehouse-desktop.cfg" ]; then
    log.info "${COL_PURPLE}${HOME}/.kamehouse/config/kamehouse-desktop.cfg${COL_MESSAGE} not found. Creating it from template"
    mkdir -p ${HOME}/.kamehouse/config/
    cp docker/setup-kamehouse/config/kamehouse-desktop.cfg ${HOME}/.kamehouse/config/kamehouse-desktop.cfg
  else
    log.info "kamehouse-desktop.cfg file exists. skipping"
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
  addHelpOption "-p" "use kamehouse git prod directory instead of current dir"
}

main "$@"
