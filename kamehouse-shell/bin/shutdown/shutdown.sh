#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  SHUTDOWN_DELAY_MIN="0"
  RESTART=false
  HIBERNATE=true
  SHUTDOWN=false
  SHUTDOWN_ACTION="/s"
}

mainProcess() {
  if ${IS_LINUX_HOST}; then
    processLin
  else
    processWin
  fi
}

processLin() {
  setSudoKameHouseCommand "/usr/sbin/shutdown"
  ${SUDO_KAMEHOUSE_COMMAND} -P ${SHUTDOWN_DELAY_MIN}
}

processWin() {
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
  local DELAY_MINUTES=${SHUTDOWN_DELAY_MIN}
  local mins=$((DELAY_MINUTES))
  log.info "Scheduled shutdown in: ${COL_PURPLE}${DELAY_MINUTES}${COL_DEFAULT_LOG} minutes with action ${COL_PURPLE}${SHUTDOWN_ACTION}${COL_DEFAULT_LOG}" 
  while [ ${mins} -gt 0 ]; do
    echo -ne "`log.info "${COL_NORMAL}Shutting down in ${COL_RED}${mins}${COL_NORMAL} minutes. Press ${COL_RED}Ctrl+C${COL_NORMAL} to abort"`\033[0K\r"
    sleep 60
    : $((mins--))
  done
}

setDelay() {
  SHUTDOWN_DELAY_MIN=$1
  local REGEX_NUMBER='^[0-9]+$'
  if [[ $SHUTDOWN_DELAY_MIN =~ $REGEX_NUMBER ]]; then
    if [ "${SHUTDOWN_DELAY_MIN}" -lt "0" ]; then
      log.error "Option -d MINUTES has an invalid value of -d ${SHUTDOWN_DELAY_MIN}"
      printHelp
      exitProcess ${EXIT_INVALID_ARG}
    fi
  else
    log.error "Option -d MINUTES has an invalid value of -d ${SHUTDOWN_DELAY_MIN}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
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
      -d)
        SHUTDOWN_DELAY_MIN="${CURRENT_OPTION_ARG}"
        ;;    
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
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-d" "shutdown delay in minutes [LIN/WIN]"
  addHelpOption "-i" "hibernate [WIN]"
  addHelpOption "-r" "restart [WIN]"
  addHelpOption "-s" "shutdown [WIN]"
}

main "$@"
