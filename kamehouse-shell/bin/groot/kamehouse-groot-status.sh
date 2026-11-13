#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/groot/groot-functions.sh

mainProcess() {
  printKameHouseGrootStatus
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    case "${CURRENT_OPTION}" in
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;
    esac
  done
}

main "$@"
