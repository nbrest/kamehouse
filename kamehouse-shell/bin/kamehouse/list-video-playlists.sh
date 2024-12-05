#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi
# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

SKIP_LOG_START_FINISH=true

mainProcess() {
  if ${IS_LINUX_HOST}; then
    find "${HOME}/${PLAYLISTS_PATH}" | grep -e "m3u" | sort
  else
    PLAYLISTS_PATH="`sed 's#/#\\\#Ig' <<<"${PLAYLISTS_PATH}"`"
    USER=`whoami`
    PLAYLISTS_FULL_PATH="${WIN_USER_HOME}\\${PLAYLISTS_PATH}"
    powershell.exe -c "cd ${PLAYLISTS_FULL_PATH}; Get-ChildItem -Recurse -Filter '*.m3u' | Select FullName"
  fi
}

# to prevent logging
parseArguments() {
  return
}

# to prevent logging
setEnvFromArguments() {
  return
}

main "$@"
