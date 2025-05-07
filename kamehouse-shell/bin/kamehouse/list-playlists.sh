#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

LOG=DISABLED

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
