#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse functions/desktop/desktop-functions.sh

initScriptConfig() {
  # Default directory to search for kamehouse desktop backgrounds
  BACKGROUNDS_SRC_DIR="${HOME}/.kamehouse/data/desktop/backgrounds"
}

initScriptEnv() {
  USE_BANNERS_DIR=false
  BANNERS_DIR=${HOME}/programs/kamehouse-desktop/lib/ui/img/banners
  BACKGROUNDS=""
}

mainProcess() {
  listAllBackgrounds
}

listAllBackgrounds() {
  log.info "Getting all backgrounds from ${BACKGROUNDS_SRC_DIR}"
  BACKGROUNDS=`find "${BACKGROUNDS_SRC_DIR}" -type f ! -regex ".*\.md$" -printf "%P\n"`
  log.info "Backgrounds:"
  echo -e "${BACKGROUNDS}"
  local COUNT=`echo -e "${BACKGROUNDS}" | wc -l`
  log.info "Total backgrounds: ${COUNT}"  
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
      --use-banners-dir)
        USE_BANNERS_DIR=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  if ${USE_BANNERS_DIR}; then
    BACKGROUNDS_SRC_DIR="${BANNERS_DIR}"
  fi
}

printHelpOptions() {
  addHelpOption "--use-banners-dir" "Use kamehouse-ui banners directory for backgrounds to check"
}

main "$@"
