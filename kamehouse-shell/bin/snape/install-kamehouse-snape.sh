#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/deploy/build-functions.sh

initScriptEnv() {
  KAMEHOUSE_SNAPE_PATH=${HOME}/programs/kamehouse-snape
  TEMP_PATH=${HOME}/temp
  KAMEHOUSE_SNAPE_SOURCE=`pwd`
}

mainProcessPre() {
  log.info "Installing ${COL_PURPLE}kamehouse-snape${COL_MESSAGE} to ${COL_PURPLE}${KAMEHOUSE_SNAPE_PATH}"
  log.info "Using directory ${COL_PURPLE}${KAMEHOUSE_SNAPE_SOURCE}${COL_MESSAGE} as the source of the scripts"
  checkSourcePath
  installKameHouseSnape
  fixPermissions
  generateBuildInfo
  installKamehouseSnapeConfig
  logKameHouseSnapeStatus
  log.info "Done installing ${COL_PURPLE}kamehouse-snape!"
}

checkSourcePath() {
  if [ ! -d "${KAMEHOUSE_SNAPE_SOURCE}/kamehouse-shell/bin" ] || [ ! -d "${KAMEHOUSE_SNAPE_SOURCE}/.git" ]; then
    log.error "This script needs to run from the root directory of a kamehouse git repository. Can't continue"
    exit ${EXIT_ERROR}
  fi
}

installKameHouseSnape() {
  log.info "Rebuilding snape scripts directory"
  rm -r -f ${KAMEHOUSE_SNAPE_PATH}/bin
  rm -f ${KAMEHOUSE_SNAPE_PATH}/conf/build-info.json
  mkdir -p ${KAMEHOUSE_SNAPE_PATH}/conf
  cp -r -f ${KAMEHOUSE_SNAPE_SOURCE}/kamehouse-snape/bin ${KAMEHOUSE_SNAPE_PATH}/
  cp -r -f ${KAMEHOUSE_SNAPE_SOURCE}/kamehouse-snape/conf ${KAMEHOUSE_SNAPE_PATH}/
  chmod -R 755 ${KAMEHOUSE_SNAPE_PATH}
}

fixPermissions() {
  log.info "Fixing scripts permissions"
  local KAMEHOUSE_SNAPE_BIN_PATH=${KAMEHOUSE_SNAPE_PATH}/bin
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
  local KAMEHOUSE_SNAPE_CONF_PATH=${KAMEHOUSE_SNAPE_PATH}/conf
  local KAMEHOUSE_BUILD_VERSION=`getKameHouseBuildVersion`
  local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
  echo '{ "buildVersion": "'${KAMEHOUSE_BUILD_VERSION}'", "buildDate": "'${BUILD_DATE}'" }' > ${KAMEHOUSE_SNAPE_CONF_PATH}/build-info.json
}

installKamehouseSnapeConfig() {
  log.info "Installing kamehouse-snape.cfg"
  if [ ! -f "${HOME}/.kamehouse/config/kamehouse-snape.cfg" ]; then
    log.info "${COL_PURPLE}${HOME}/.kamehouse/config/kamehouse-snape.cfg${COL_MESSAGE} not found. Creating it from template"
    mkdir -p ${HOME}/.kamehouse/config/
    cp docker/setup-kamehouse/config/kamehouse-snape.cfg ${HOME}/.kamehouse/config/kamehouse-snape.cfg
  else
    log.info "kamehouse-snape.cfg file exists. skipping"
  fi
}

logKameHouseSnapeStatus() {
  log.info "Deployed kamehouse-snape status"
  log.info "ls -lh ${COL_CYAN_STD}${KAMEHOUSE_SNAPE_PATH}"
  ls -lh "${KAMEHOUSE_SNAPE_PATH}"
  log.info "${COL_YELLOW_STD}kamehouse-snape version:"
  echo -ne "${COL_YELLOW_STD}     "
  cat "${KAMEHOUSE_SNAPE_PATH}/conf/build-info.json"
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
        KAMEHOUSE_SNAPE_SOURCE=${HOME}/git/kamehouse
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
