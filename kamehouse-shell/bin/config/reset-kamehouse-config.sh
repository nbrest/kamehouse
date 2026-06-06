#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
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
  mkdir -p ${HOME}/temp
  mv ${KAMEHOUSE_CFG} ${HOME}/temp/${KAMEHOUSE_CFG_TEMP}
  ls -lh ${HOME}/temp/${KAMEHOUSE_CFG_TEMP}
}

resetConfigFromTemplate() {
  log.info "Resetting kamehouse config from template"
  cd ${HOME}/git/kamehouse
  git pull origin dev
  checkCommandStatus "$?" "An error occurred pulling kamehouse repo"
  cp ./docker/setup-kamehouse/config/kamehouse.cfg ${KAMEHOUSE_CFG}
  ls -lh ${KAMEHOUSE_CFG}
}

showOldConfigCommands() {
  log.info "See old config with:" 
  log.info "  vim ${HOME}/temp/${KAMEHOUSE_CFG_TEMP}"
  log.info "Remove old temp configs with:"
  log.info "  remove-temp-kamehouse-configs.sh"
}

main "$@"
