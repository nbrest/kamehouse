#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then echo "Error importing docker-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Started rebuilding kamehouse docker image"
  
  setKameHouseRootProjectDir

  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-build-push-all-kamehouse.sh 
  checkCommandStatus "$?" "Error rebuilding and pushing the kamehouse docker image" 
  
  log.info "Finished rebuilding kamehouse docker image"
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
      -c)
        USE_CURRENT_DIR=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-c" "rebuild the docker image using the current directory. Default dir: ${PROJECT_DIR}"
}

main "$@"
