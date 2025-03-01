#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

USERHOME_WIN="${HOME}"
# USERHOME_LIN gets set during install kamehouse-shell
USERHOME_LIN="/home/${DEFAULT_KAMEHOUSE_USERNAME}"

mainProcess() {
  # WIN_USER_HOME=`cmd.exe '/c echo %USERPROFILE%'`
  # WIN_USER_HOME=${WIN_USER_HOME::-1}

  if ${IS_LINUX_HOST}; then
    export HOME="${USERHOME_LIN}"
  else
    export HOME="${USERHOME_WIN}"
  fi
  log.trace "HOME=${HOME}"
}

main "$@"
