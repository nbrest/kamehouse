#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/desktop/desktop-functions.sh
if [ "$?" != "0" ]; then echo "Error importing desktop-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  USE_BANNERS_DIR=false
  BANNERS_DIR=${HOME}/programs/kamehouse-desktop/lib/ui/img/banners
  BACKGROUNDS=""
  BACKGROUNDS_SRC_DIR="${HOME}/.kamehouse/data/desktop/backgrounds"
  EXCLUDED_FILES="000_SlideShow_README.md"
}

mainProcess() {
  initOutputFile
  getAllBackgrounds
  listUnprocessedBackgrounds
}

initOutputFile() {
  echo -ne "" > ${KAMEHOUSE_DESKTOP_BACKGROUNDS_UNPROCESSED_FILE}
}

getAllBackgrounds() {
  log.info "Getting all backgrounds"
  if ${USE_BANNERS_DIR}; then
    BACKGROUNDS=`find ${BANNERS_DIR} -type f -exec basename {} \;`
  else
    BACKGROUNDS=`ls -1 "${BACKGROUNDS_SRC_DIR}" | grep -v "${EXCLUDED_FILES}"`
  fi
  local COUNT=`echo -e "${BACKGROUNDS}" | wc -l`
  log.info "Total backgrounds: ${COUNT}"  
}

listUnprocessedBackgrounds() {
  log.info "Checking for unprocessed backgrounds"
  while read BACKGROUND; do
    if [ -n "${BACKGROUND}" ]; then
      cat ${KAMEHOUSE_DESKTOP_BACKGROUNDS_SUCCESS_FILE} | grep -e ".*${BACKGROUND}$" > /dev/null
      if [ "$?" != "0" ]; then
        echo "${BACKGROUND}" >> ${KAMEHOUSE_DESKTOP_BACKGROUNDS_UNPROCESSED_FILE}
      fi
    fi
  done <<< ${BACKGROUNDS}
  log.info "List of unprocessed backgrounds"
  cat ${KAMEHOUSE_DESKTOP_BACKGROUNDS_UNPROCESSED_FILE} 
  local NUMBER=`cat ${KAMEHOUSE_DESKTOP_BACKGROUNDS_UNPROCESSED_FILE} | wc -l`
  log.info "Unprocessed backgrounds count: ${COL_RED}${NUMBER}"  
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

printHelpOptions() {
  addHelpOption "--use-banners-dir" "Use kamehouse-ui banners directory for backgrounds to check"
}

main "$@"
