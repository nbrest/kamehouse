#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/desktop/desktop-functions.sh
if [ "$?" != "0" ]; then echo "Error importing desktop-functions.sh" ; exit 99 ; fi

mainProcess() {
  showSuccessBackgrounds
  showErrorBackgrounds
}

showSuccessBackgrounds() {
  log.info "List of successfully processed backgrounds"
  cat ${KAMEHOUSE_DESKTOP_BACKGROUNDS_SUCCESS_FILE} 
  local NUMBER=`cat ${KAMEHOUSE_DESKTOP_BACKGROUNDS_SUCCESS_FILE} | wc -l`
  log.info "Successfully processed backgrounds count: ${COL_BLUE}${NUMBER}"
}

showErrorBackgrounds() {
  log.info "List of invalid backgrounds"
  cat ${KAMEHOUSE_DESKTOP_BACKGROUNDS_ERROR_FILE} 
  local NUMBER=`cat ${KAMEHOUSE_DESKTOP_BACKGROUNDS_ERROR_FILE} | wc -l`
  log.info "Invalid backgrounds count: ${COL_RED}${NUMBER}"
}

main "$@"
