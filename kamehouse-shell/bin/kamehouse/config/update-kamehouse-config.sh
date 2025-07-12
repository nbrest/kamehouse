#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  CONFIG_KEY=""
  CONFIG_VALUE=""
}

mainProcess() {
  checkConfigKeyIsValid
  printExistingValue
  updateConfig
  printUpdatedConfig
}

checkConfigKeyIsValid() {
  log.info "Checking for valid config key ${CONFIG_KEY}"
  cat ${KAMEHOUSE_CFG} | grep "${CONFIG_KEY}" > /dev/null
  if [ "$?" != "0" ]; then 
    log.error "${CONFIG_KEY} not found in kamehouse.cfg"
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

printExistingValue() {
  log.info "The current value of ${CONFIG_KEY} is: ${!CONFIG_KEY}. Updating the value to ${CONFIG_VALUE}"
}

updateConfig() {
  sed -i -E "s/^#${CONFIG_KEY}=.*/${CONFIG_KEY}=/I" ${KAMEHOUSE_CFG}
  sed -i -E "s#^${CONFIG_KEY}=.*#${CONFIG_KEY}=${CONFIG_VALUE}#I" ${KAMEHOUSE_CFG}
}

printUpdatedConfig() {
  log.info "Updated ${CONFIG_KEY} in kamehouse.cfg"
  cat ${HOME}/.kamehouse/config/kamehouse.cfg | grep "${CONFIG_KEY}="  
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
      -k)
        CONFIG_KEY="${CURRENT_OPTION_ARG}"
        ;;
      -v)
        CONFIG_VALUE="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  if [ -z "${CONFIG_KEY}" ]; then
    log.error "Option -k is required"
    printHelpMenu
    exit ${EXIT_INVALID_ARG}
  fi

  if [ -z "${CONFIG_VALUE}" ]; then
    log.error "Option -v is required"
    printHelpMenu
    exit ${EXIT_INVALID_ARG}
  fi
}

printHelpOptions() {
  addHelpOption "-k" "kamehouse config key to update" "r"
  addHelpOption "-v" "value to set on the key" "r"
}

main "$@"
