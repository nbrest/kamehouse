#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/kamehouse/desktop/desktop-functions.sh

mainProcess() {
  rm -f ${KAMEHOUSE_DESKTOP_BACKGROUNDS_SUCCESS_FILE}
  touch ${KAMEHOUSE_DESKTOP_BACKGROUNDS_SUCCESS_FILE}

  rm -f ${KAMEHOUSE_DESKTOP_BACKGROUNDS_ERROR_FILE}
  touch ${KAMEHOUSE_DESKTOP_BACKGROUNDS_ERROR_FILE}

  rm -f ${KAMEHOUSE_DESKTOP_BACKGROUNDS_UNPROCESSED_FILE}
  touch ${KAMEHOUSE_DESKTOP_BACKGROUNDS_UNPROCESSED_FILE}
}


main "$@"
