#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  RESTART=false
  HIBERNATE=true
  SHUTDOWN=false
  SHUTDOWN_ACTION="/h"
  DELAY=
}

mainProcess() {
  shutdownProcess &
  wait
}

shutdownProcess() {
  countdown
  if [ "${HIBERNATE}" == "true" ]; then
    powershell.exe -c "shutdown.exe ${SHUTDOWN_ACTION} /f"
  else
    powershell.exe -c "shutdown.exe ${SHUTDOWN_ACTION} /f /t 0"
  fi
}

countdown() {
  DELAY_MINUTES=${DELAY}
  mins=$((DELAY_MINUTES))
  log.info "Scheduled shutdown in: ${COL_PURPLE}${DELAY_MINUTES}${COL_DEFAULT_LOG} minutes with action ${COL_PURPLE}${SHUTDOWN_ACTION}${COL_DEFAULT_LOG}" 
  while [ ${mins} -gt 0 ]; do
    echo -ne "`log.info "${COL_NORMAL}Shutting down in ${COL_RED}${mins}${COL_NORMAL} minutes. Press ${COL_RED}Ctrl+C${COL_NORMAL} to abort"`\033[0K\r"
    sleep 60
    : $((mins--))
  done
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
      -i)
        HIBERNATE=true
        RESTART=false
        SHUTDOWN=false
        SHUTDOWN_ACTION="/h"
        ;;
      -r)
        HIBERNATE=false
        RESTART=true
        SHUTDOWN=false
        SHUTDOWN_ACTION="/r"
        ;;
      -s)
        HIBERNATE=false
        RESTART=false
        SHUTDOWN=true
        SHUTDOWN_ACTION="/s"
        ;;
      -t)
        setDelay "${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setDelay() {
  DELAY=$1
  local REGEX_NUMBER='^[0-9]+$'
  if [[ $DELAY =~ $REGEX_NUMBER ]]; then
    if [ "${DELAY}" -lt "0" ]; then
      log.error "Option -t MINUTES has an invalid value of -t ${DELAY}"
      printHelp
      exitProcess ${EXIT_INVALID_ARG}
    fi
  else
    log.error "Option -t MINUTES has an invalid value of -t ${DELAY}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

setEnvFromArguments() {
  checkRequiredOption "-t" "${DELAY}"
}

printHelpOptions() {
  addHelpOption "-i" "hibernate"
  addHelpOption "-r" "restart"
  addHelpOption "-s" "shutdown"
  addHelpOption "-t 999" "number of MINUTES to delay shutdown" "r"
}

main "$@"
