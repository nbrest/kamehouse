#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  local SUFFIX=$RANDOM
  local CURRENT_DATE=$(date +%Y-%m-%d)
  KAMEHOUSE_CFG_TEMP="kamehouse-${CURRENT_DATE}-${SUFFIX}.cfg"
}

mainProcess() {
  moveCurrentConfigToTemp
  resetConfigFromTemplate
  showOldConfigCommands
}

moveCurrentConfigToTemp() {
  log.info "Moving current config to temp"
  mkdir -p ${HOME}/.kamehouse/var/tmp
  mv ${KAMEHOUSE_CFG} ${HOME}/.kamehouse/var/tmp/${KAMEHOUSE_CFG_TEMP}
  ls -lh ${HOME}/.kamehouse/var/tmp/${KAMEHOUSE_CFG_TEMP}
}

resetConfigFromTemplate() {
  log.info "Resetting kamehouse config from template"
  cd ${HOME}/.kamehouse/git/kamehouse
  execWithTimeout ${GIT_TIMEOUT} git pull origin dev
  checkCommandStatus "$?" "An error occurred pulling kamehouse repo"
  cp ./docker/setup-kamehouse/conf/kamehouse.cfg ${KAMEHOUSE_CFG}
  ls -lh ${KAMEHOUSE_CFG}
}

showOldConfigCommands() {
  log.info "See old config with:" 
  log.info "  vim ${HOME}/.kamehouse/var/tmp/${KAMEHOUSE_CFG_TEMP}"
  log.info "Remove old temp configs with:"
  log.info "  remove-temp-kamehouse-configs.sh"
}

main "$@"
