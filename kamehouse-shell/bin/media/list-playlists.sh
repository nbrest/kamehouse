#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

mainProcessLin() {
  find "${HOME}/${PLAYLISTS_PATH}" | grep -e "m3u" | sort
}

mainProcessWin() {
  PLAYLISTS=`find "${HOME}/${PLAYLISTS_PATH}" | grep -e "m3u" | sort`
  PLAYLISTS="`sed 's#/c/Users/#C:/Users/#Ig' <<<"${PLAYLISTS}"`"
  echo -e "${PLAYLISTS}"
}

main "$@"
