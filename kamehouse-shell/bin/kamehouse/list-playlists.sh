#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOG=DISABLED
}

mainProcess() {
  if ${IS_LINUX_HOST}; then
    find "${HOME}/${PLAYLISTS_PATH}" | grep -e "m3u" | sort
  else
    PLAYLISTS=`find "${HOME}/${PLAYLISTS_PATH}" | grep -e "m3u" | sort`
    PLAYLISTS="`sed 's#/c/Users/#C:/Users/#Ig' <<<"${PLAYLISTS}"`"
    echo -e "${PLAYLISTS}"
  fi
}

main "$@"
